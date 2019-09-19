package com.ts.upgrade

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UpgradeDateFormatUtil {

    companion object {
        const val yyyyMMdd = "yyyyMMdd"
        const val yyyyMd = "yyyyMd"
        const val yyyy_MM = "yyyy-MM"
        const val yyyy_MM_dd = "yyyy-MM-dd"
        const val yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm"
        const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
        const val yyyy_M_d = "yyyy-M-d"
        const val yyyyYMMYddR = "yyyy年MM月dd日"
        const val yyyyYMYdR = "yyyy年M月d日"

        fun format(pattern: String): String {
            return format(pattern, Date())
        }

        fun format(pattern: String, timeInMillis: Long): String {
            val date = Date(timeInMillis)
            return format(pattern, date)
        }

        fun format(pattern: String, cal: Calendar): String {
            return format(pattern, cal.time)
        }

        fun format(pattern: String, date: Date): String {
            val dateFormat = SimpleDateFormat(pattern, Locale.CHINA)
            return dateFormat.format(date)
        }

        fun parse(pattern: String, text: String): Date {
            val dateFormat = SimpleDateFormat(pattern, Locale.CHINA)
            return try {
                dateFormat.parse(text)
            } catch (e: ParseException) {
                e.printStackTrace()
                Date()
            }
        }
    }

}
