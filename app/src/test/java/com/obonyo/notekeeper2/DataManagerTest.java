package com.obonyo.notekeeper2;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() {
        DataManager Dm = DataManager.getInstance();
        final CourseInfo course= Dm.getCourse("android_async");
        final String noteTitle="Test note title";
        final String noteText="This is the body text of my note title";

        int noteIndex=Dm.createNewNote();
        NoteInfo newNote=Dm.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNote= Dm.getNotes().get(noteIndex);
//        assertSame(newNote,compareNote);
        assertEquals(compareNote.getCourse(),course);
        assertEquals(compareNote.getTitle(),noteTitle);
        assertEquals(compareNote.getText(),noteText);
    }
}