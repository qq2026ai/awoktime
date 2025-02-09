package cn.jianyun.worktime.module.timework.views.project


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.vm.TimeworkProjectViewModel
import cn.jianyun.worktime.ui.component.nav.SwipeDeleteView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.OkAndCancelButtonGroup
import cn.jianyun.worktime.ui.component.form.SheetDialog
import cn.jianyun.worktime.ui.component.form.SheetModel
import cn.jianyun.worktime.ui.component.form.ShowInputDialog
import cn.jianyun.worktime.ui.component.form.SortDialogView
import cn.jianyun.worktime.ui.component.nav.EmptyDataView
import cn.jianyun.worktime.ui.component.nav.PageWithFooterView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkProjectManageView(navHostController: NavHostController) {

    var viewModel = hiltViewModel<TimeworkProjectViewModel>()
    viewModel.tryReload()

    PageWithFooterView(
        navHostController = navHostController,
        title = "项目管理",
        footer = {
            if(viewModel.datalist.size > 1){
                OkAndCancelButtonGroup(okLabel = "添加项目", cancelLabel = "项目排序", okAction = {
                    viewModel.editItem = TimeworkProject()
                    viewModel.formType = FormType.add()
                }) {
                    viewModel.formType = FormType.sort()
                }
            }
            else{
                LongOkButton("添加项目") {
                    viewModel.editItem = TimeworkProject()
                    viewModel.formType = FormType.add()
                }
            }
        }) {

        if(viewModel.datalist.isEmpty()){
            EmptyDataView(label = "暂无项目")
        }
        else{
            viewModel.datalist.forEach{
                SwipeDeleteView( sid=it.uuid,  onEdit = {
                    viewModel.editItem = it
                    viewModel.formType = FormType(type="detail")
                }, deleteMessage = "删除项目，会把项目下所有工时数据级联删除，确定还要继续吗？", onDelete = {
                    if(viewModel.datalist.size == 1){
                        viewModel.toast("至少保留一个项目")
                    }
                    else{
                        viewModel.editItem = it
                        viewModel.doDelete()
                    }
                }) {
                    Column(modifier=Modifier.padding(15.dp)) {
                        VerticalRow {
                            Text(it.name)
                            Blank()
                            if(it.uuid == viewModel.currentProjectId){
                                TagView(tag = "当前项目")
                            }
                        }
                    }
                }
            }
        }

        if(viewModel.formType.isAdd() || viewModel.formType.isUpdate()){
            ShowInputDialog(
                value = viewModel.editItem.name,
                multiLine = false,
                onValueChange = {
                    if(it != ""){
                        viewModel.editItem = viewModel.editItem.copy(name=it)
                        viewModel.save()
                        true
                    }
                    else{
                        false
                    }
                },
                onDismiss = {
                    viewModel.resetForm()
                }
            )
        }

        if(viewModel.formType.isForm("detail")){
            SheetDialog(title="选择一项操作", onDismissRequest = {
                viewModel.resetForm()
            }, listOf(SheetModel("切换为当前项目", color= ThemeColor, shown= viewModel.editItem.uuid != viewModel.currentProjectId, action = {
                viewModel.makeCurrentProject()
            }),SheetModel("编辑项目名称", color= ThemeColor, action = {
                viewModel.formType = FormType.edit()
            }), SheetModel("删除项目", color = DeleteColor, shown=viewModel.datalist.size > 1, action = {
                viewModel.formType = FormType.delete()
            })))
        }

        if(viewModel.formType.isDelete()) {
            DeleteDialog(title = "删除项目，会把项目下所有工时数据级联删除，确定还要继续吗",okAction = {
                viewModel.doDelete()
            }) {
                viewModel.resetForm()
            }
        }

        if(viewModel.formType.isSort()) {
            SortDialogView(title = "项目排序", options = viewModel.datalist.map{it.toSelect()}, onChange = {
                viewModel.doSort(it)
                viewModel.formType = FormType()
            }) {
                viewModel.formType = FormType()
            }
        }

    }



}