package com.neo.notekeeperkotlin

// file too hold courseInfo and noteInfo class

/**
 * class handling courses
 */
data class CourseInfo(val courseId: String, val title: String) {
    // need to be overridden since it's what ret value is what spinner shows
    override fun toString(): String {
        return title;
    }
}


/**
 * class handling notes and allows the params to be nullable(?) for creation of newNote
 */
data class NoteInfo(var course: CourseInfo? = null, var title: String? = null, var text: String? = null) {

}
