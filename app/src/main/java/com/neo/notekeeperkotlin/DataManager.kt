package com.neo.notekeeperkotlin


/**
 * singleton class handles data management operations of courseInfo and noteInfo obj
 * i.e creating instances of both classes and management
 */
object DataManager {
    // prop
    val courses = HashMap<String, CourseInfo>()         // hashMap or dict holding courseId: CourseInfo OBJ
    val notes = ArrayList<NoteInfo>()                   // list of noteInfo obj

    // initializer block
    init {
        initializeCourses()
        initializeNotes()
    }


    /**
     * populates courses HashMap
     */
    private fun initializeCourses(){
        var course = CourseInfo("android_intents", "Android Programming with Intents")
        courses.set(course.courseId, course)

        course = CourseInfo("android_async", "Android Async Programming and Services")
        courses.set(course.courseId, course)

        course = CourseInfo(title = "Java Fundamentals: The Java Language", courseId = "java_lang")         // init using named params
        courses.set(course.courseId, course)

        course = CourseInfo("java_core", "Java Fundamentals: The core Platform")
        courses.set(course.courseId, course)
    }


    /**
     * populates the noteList
     */
    private fun initializeNotes() {
        var course = courses["android_intents"]!!               // gets course obj associated with this course Id or key
        var note = NoteInfo(course, "Dynamic intent resolution",
            "Wow, intents allow components to be resolved at runtime")
        notes.add(note)
        note = NoteInfo(course, "Delegating intents",
            "PendingIntents are powerful; they delegate much more than just a component invocation")
        notes.add(note)

        course = courses["android_async"]!!
        note = NoteInfo(course, "Service default threads",
            "Did you know that by default an Android Service will tie up the UI thread?")
        notes.add(note)
        note = NoteInfo(course, "Long running operations",
            "Foreground Services can be tied to a notification icon")
        notes.add(note)

        course = courses["java_lang"]!!
        note = NoteInfo(course, "Parameters",
            "Leverage variable-length parameter lists")
        notes.add(note)
        note = NoteInfo(course, "Anonymous classes",
            "Anonymous classes simplify implementing one-use types")
        notes.add(note)

        course = courses["java_core"]!!
        note = NoteInfo(course, "Compiler options",
            "The -jar option isn't compatible with with the -cp option")
        notes.add(note)
        note = NoteInfo(course, "Serialization",
            "Remember to include SerialVersionUID to assure version compatibility")
        notes.add(note)
    }


}