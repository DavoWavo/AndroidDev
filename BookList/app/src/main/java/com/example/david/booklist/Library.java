package com.example.david.booklist;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Library {

    private static Library sLibrary;

    private HashMap<String, Book> mLibrary;

    public static Library get(Context context) {
        if (sLibrary == null) {
            sLibrary = new Library(context);
        }
        return sLibrary;
    }

    private Library(Context context) {
        mLibrary = new HashMap<String, Book>();
        mLibrary.put("Dune", new Book());
        mLibrary.put("Nineteen Eighty-Four", new Book());
        mLibrary.put("Ender's game", new Book());
        mLibrary.put("The Left Hand of Darkness", new Book());
        mLibrary.put("The Time Machine", new Book());
        mLibrary.put("Fahrenheit 451", new Book());
        mLibrary.put("The Forever War", new Book());
        mLibrary.put("Hyperion", new Book());
        mLibrary.put("Brave New World", new Book());
        mLibrary.put("The Hitchhiker's Guide to the Galaxy", new Book());
    }

    public HashMap<String, Book> getmBooks() {
        return mLibrary;
    }

    public Book getBook(String name) {
        if(mLibrary.containsKey(name)) {
            return mLibrary.get(name);
        }
        return null;
    }

    public List<String> getBookNames() {
        return new ArrayList<String>(mLibrary.keySet());
    }
}