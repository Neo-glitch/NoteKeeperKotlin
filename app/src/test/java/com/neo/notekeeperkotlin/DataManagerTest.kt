package com.neo.notekeeperkotlin

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DataManagerTest {

    // fun for test PreProcessing(before each test0
    @Before
    fun setUp() {

        // clears array of notes after each test and inti with default set of notes
        DataManager.notes.clear()
        DataManager.initializeNotes()
    }

    // test for addNote fun in DataManager class and can be a diff name
    @Test
    fun addNote() {
        // "!!" means that if course is not null assign to val, else if it is null throw an exception
        val course = DataManager.courses.get("android_async")!!
        val noteTitle = "This is a test note"
        val noteText = "This is the body of my test note"

        val index = DataManager.addNote(course, noteTitle, noteText)
        val note = DataManager.notes[index]

        // expected value vs the value used to check(actual value retrieved)
        assertEquals(course, note.course)
        assertEquals(noteTitle, note.title)
        assertEquals(noteText, note.text)
    }


    // test for findNotes fun in DataManager class
    @Test
    fun findSimilarNotes(){
        val course = DataManager.courses.get("android_async")!!
        val noteTitle = "This is a test note"
        val noteText1 = "This is the body of my test note"
        val noteText2 = "Thi is the body of my second test note"

        // gets index of two notes create
        val index1 = DataManager.addNote(course, noteTitle, noteText1)
        val index2 = DataManager.addNote(course, noteTitle, noteText2)

        val note1 = DataManager.findNote(course, noteTitle, noteText1)
        val foundIndex1 = DataManager.notes.indexOf(note1)
        assertEquals(index1, foundIndex1)

        val note2 = DataManager.findNote(course, noteTitle, noteText2)
        val foundIndex2 = DataManager.notes.indexOf(note2)
        assertEquals(index2, foundIndex2)
    }
}