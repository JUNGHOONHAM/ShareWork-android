package com.hampson.sharework.common

object RegularExpression {

    // 휴대폰 번호 정규식
    val phoneNumberReg = Regex("01[016789][0-9]{3,4}[0-9]{4}")

    // 주민등록번호 앞자리 정규식
    val residentNumberReg = Regex("^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|[3][01])")

    // 주민등록번호 뒷자리 정규식
    val genderNumberReg = Regex("[0-4]")
}
