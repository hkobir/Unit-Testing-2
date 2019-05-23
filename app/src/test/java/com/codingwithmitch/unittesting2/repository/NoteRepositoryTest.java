package com.codingwithmitch.unittesting2.repository;

import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.unittesting2.models.Note;
import com.codingwithmitch.unittesting2.persistence.NoteDao;
import com.codingwithmitch.unittesting2.ui.Resource;
import com.codingwithmitch.unittesting2.util.InstantExecutorExtension;
import com.codingwithmitch.unittesting2.util.LiveDataTestUtil;
import com.codingwithmitch.unittesting2.util.TestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static com.codingwithmitch.unittesting2.repository.NoteRepository.DELETE_FAILURE;
import static com.codingwithmitch.unittesting2.repository.NoteRepository.DELETE_SUCCESS;
import static com.codingwithmitch.unittesting2.repository.NoteRepository.INSERT_FAILURE;
import static com.codingwithmitch.unittesting2.repository.NoteRepository.INSERT_SUCCESS;
import static com.codingwithmitch.unittesting2.repository.NoteRepository.UPDATE_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(InstantExecutorExtension.class)
class NoteRepositoryTest {

    private NoteDao noteDao;

    // system under test
    private NoteRepository noteRepository;


    @BeforeEach
    public void initEach(){
        noteDao = mock(NoteDao.class);
        noteRepository = new NoteRepository(noteDao);
    }


    // ------------------------------------------------------------------------------------
    // Insert and Update
    //-------------------------------------------------------------------------------------

    /*
        insert notes
        verify correct method is called
        confirm Observer is triggered
        confirm new rows inserted
     */
    @Test
    void insertNote_returnRows() throws Exception {

        // Arrange
        final Long insertedRow = 1L;
        final Single<Long> returnedData = Single.just(insertedRow);
        when(noteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        // Act
        Note note = new Note(TestUtil.TEST_NOTE_1);
        final Resource<Integer> returnedValue = noteRepository.insertNote(note).blockingFirst();

        // Assert
        verify(noteDao).insertNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        assertEquals(Resource.success(1, INSERT_SUCCESS), returnedValue);
    }

    /*
        Insert Note
        Failure (return -1)

     */
    @Test
    void insertNote_returnFailure() throws Exception {

        // Arrange
        final Long failedInsert = -1L;
        final Single<Long> returnedData = Single.just(failedInsert);
        when(noteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        // Act
        Note note = new Note(TestUtil.TEST_NOTE_1);
        final Resource<Integer> returnedValue = noteRepository.insertNote(note).blockingFirst();

        // Assert
        verify(noteDao).insertNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        assertEquals(Resource.error(INSERT_FAILURE, null), returnedValue);
    }


    /*
        insert note
        null title
        confirm throw exception
     */
    @Test
    void insertNotes_nullTitle_throwException() throws Exception {

        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(TestUtil.TEST_NOTE_1);
                note.setTitle(null);
                noteRepository.insertNote(note);
            }
        });
    }


    /*
        update note
        verify correct method is called
        confirm Observer is triggered
        confirm number of rows is updated
     */

    @Test
    void updateNote_returnNumRowsUpdated() throws Exception {
        // Arrange
        final int updatedRow = 1;
        when(noteDao.updateNote(any(Note.class))).thenReturn(Single.just(updatedRow));

        // Act
        Note note = new Note(TestUtil.TEST_NOTE_1);
        final Resource<Integer> returnedValue = noteRepository.updateNote(note).blockingFirst();

        // Assert
        verify(noteDao).updateNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        assertEquals(Resource.success(updatedRow, UPDATE_SUCCESS), returnedValue);
    }

    /*
        update note
        null title
        confirm throw exception
    */
    @Test
    void updateNote_nullTitle_throwException() throws Exception {

        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(TestUtil.TEST_NOTE_1);
                note.setTitle(null);
                noteRepository.updateNote(note);
            }
        });
    }


    // ------------------------------------------------------------------------------------
    // Retrieve and delete
    //-------------------------------------------------------------------------------------

    /*
        delete note
        null id
        throw exception
     */
    @Test
    void deleteNote_nullId_throwException() throws Exception {
        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(TestUtil.TEST_NOTE_1);
                note.setId(-1);
                noteRepository.deleteNote(note);
            }
        });
    }

    /*
        delete note
        delete success
        return Resource.success with deleted row
     */
    @Test
    void deleteNote_deleteSuccess_returnResourceSuccess() throws Exception {
        // Arrange
        final int deletedRow = 1;
        Resource<Integer> successResponse = Resource.success(deletedRow, DELETE_SUCCESS);
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(noteDao.deleteNote(any(Note.class))).thenReturn(Single.just(deletedRow));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(note));

        // Assert
        assertEquals(successResponse, observedResponse);
    }

    /*
        delete note
        delete failure
        return Resource.error
     */
    @Test
    void deleteNote_deleteFailure_returnResourceError() throws Exception {
        // Arrange
        final int row = -1;
        Resource<Integer> errorResponse = Resource.error( null, DELETE_FAILURE);
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(noteDao.deleteNote(any(Note.class))).thenReturn(Single.just(row));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(note));

        // Assert
        assertEquals(errorResponse, observedResponse);
    }

    /*
        retrieve notes
        return list of notes
     */

    @Test
    void getNotes_returnListWithNotes() throws Exception {
        // Arrange
        List<Note> notes = TestUtil.TEST_NOTES_LIST;
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(noteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(noteRepository.getNotes());


        // Assert
        assertEquals(notes, observedData);
    }

    /*
        retrieve notes
        return empty list
     */

    @Test
    void getNotes_returnEmptyList() throws Exception {
        // Arrange
        List<Note> notes = new ArrayList<>();
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(noteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(noteRepository.getNotes());


        // Assert
        assertEquals(notes, observedData);
    }
}





















