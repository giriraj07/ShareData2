package com.example.clientreceiver;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    int cnt=0;
    static final Uri CONTENT_URL =
            Uri.parse("content://com.example.girirajkishor.sharedcontactprovider/cpcontacts");

    TextView contactsTextView = null;
    EditText deleteIDEditText, idLookupEditText, addNameEditText;
    CursorLoader cursorLoader;

    // Provides access to other applications Content Providers
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolver = getContentResolver();
        contactsTextView = (TextView) findViewById(R.id.contactsTextView);
        deleteIDEditText = (EditText) findViewById(R.id.deleteIDEditText);
        idLookupEditText = (EditText) findViewById(R.id.idLookupEditText);
        addNameEditText = (EditText) findViewById(R.id.addNameEditText);
        getContacts();
    }

    public void getContacts()
    {
        String[] projection = new String[]{"id","value"};

        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);
        String contactList = "";
        // Cycle through and display every row of data
        if(cursor.moveToNext()){
            do{
                 String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("value"));
                contactList = contactList + id + " : " + name + "\n";
            }while (cursor.moveToNext());
        }
        contactsTextView.setText(contactList);
    }

    public void deleteContact(View view) {
        String idToDelete = deleteIDEditText.getText().toString();
        // Use the resolver to delete ids by passing the content provider url
        // what you are targeting with the where and the string that replaces
        // the ? in the where clause
        long idDeleted = resolver.delete(CONTENT_URL,
                "id = ? ", new String[]{idToDelete});
        getContacts();
    }

    public void lookupContact(View view) {

        // The id we want to search for
        String idToFind = idLookupEditText.getText().toString();

        // Holds the column data we want to retrieve
        String[] projection = new String[]{"id", "key","value"};

        // Pass the URL for Content Provider, the projection,
        // the where clause followed by the matches in an array for the ?
        // null is for sort order
        Cursor cursor = resolver.query(CONTENT_URL,
                projection, "id = ? ", new String[]{idToFind}, null);

        String contact = "";

        // Cycle through our one result or print error
        if(cursor.moveToFirst()){

            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("value"));
            contact = contact + id + " : " + name + "\n";
        } else {

            Toast.makeText(this, "Contact Not Found", Toast.LENGTH_SHORT).show();

        }
        contactsTextView.setText(contact);
    }
    public void showContacts(View view) {
        getContacts();
    }
    public void addContact(View view) {
        String nameToAdd = addNameEditText.getText().toString();
        // Put in the column name and the value
        ContentValues values = new ContentValues();
        values.put("key",nameToAdd+cnt);
        values.put("value",nameToAdd);
         cnt++;
        resolver.insert(CONTENT_URL, values);
        getContacts();
    }
}