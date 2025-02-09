package cn.jianyun.worktime.util

import java.util.regex.Matcher
import java.util.regex.Pattern


fun isValidEmail(email: String): Boolean{
    val emailPattern = "^.+@.+\\.[A-Za-z]+$"
    val pattern: Pattern = Pattern.compile(emailPattern)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}