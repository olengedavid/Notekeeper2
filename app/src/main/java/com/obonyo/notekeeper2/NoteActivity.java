package com.obonyo.notekeeper2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String TAG= NoteActivity.class.getSimpleName();
    public static  final String NOTE_POSITION ="com.obonyo.notekeeper2.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.obonyo.notekeeper2.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE ="com.obonyo.notekeeper2.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT ="com.obonyo.notekeeper2.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Spinner spinner_courses;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses=DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        mSpinnerCourses.setAdapter(adapterCourses);
        readDisplayStateValues();
        if(savedInstanceState==null){
            saveOriginalNoteValue();
        }else
            restoreOriginalNoteValue(savedInstanceState);



        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);
        if(!mIsNewNote)
            disPlayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_note,menu);
        return true;
    }

    private void restoreOriginalNoteValue(Bundle savedInstanceState) {
        mOriginalNoteCourseId=savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle=savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText=savedInstanceState.getString(ORIGINAL_NOTE_TEXT);


    }

    private void saveOriginalNoteValue() {
        if(mIsNewNote){
            return;
        }
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle =mNote.getTitle();
        mOriginalNoteText =mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            Log.i(TAG, "Cancelling note at position "+mNotePosition);
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);
            }else
                storePreviousNoteValues();
        }else
            saveNote();
        Log.d(TAG, "onPause: ");
    }

    private void storePreviousNoteValues() {
            CourseInfo course=DataManager.getInstance().getCourse(mOriginalNoteCourseId);
            mNote.setCourse(course);
            mNote.setTitle(mOriginalNoteTitle);
            mNote.setText(mOriginalNoteText);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem()) ;
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void disPlayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses=DataManager.getInstance().getCourses();
         int courseIndex=courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    private void readDisplayStateValues() {
        Intent intent=getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote =position==POSITION_NOT_SET;
        if(mIsNewNote){
            createNewNote();
        }else
            mNote=DataManager.getInstance().getNotes().get(position);
    }

    private void createNewNote() {
        DataManager dm= DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote=dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_send_mail) {
            sendEmail();
            return true;
        }else if(id==R.id.action_cancel){
            mIsCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        CourseInfo course= (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject= mTextNoteTitle.getText().toString();
        String text="Checkout what i learned in the pluralsight course \"" +
                course.getTitle()+ "\"\n" + mNote.getText().toString();
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(intent.EXTRA_SUBJECT,subject);
        intent.putExtra(intent.EXTRA_TEXT,text);
        startActivity(intent);
    }
}