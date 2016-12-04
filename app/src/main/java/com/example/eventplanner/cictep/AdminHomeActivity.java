package com.example.eventplanner.cictep;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eventplanner.cictep.database.DBHelper;
import com.example.eventplanner.cictep.database.DBInfo;
import com.example.eventplanner.cictep.database.DBProvider;

public class AdminHomeActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        dbHelper = new DBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.action_logout:
                dialogError("", "Are you sure you want to logout?");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogError(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = DBProvider.fetchUser(dbHelper, new String[] {DBInfo.TableUser.USERNAME},
                        DBInfo.TableUser.ISLOGGEDIN, "1");
                cursor.moveToFirst();
                String username = cursor.getString(cursor.getColumnIndex(DBInfo.TableUser.USERNAME));

                DBProvider.updateUser(dbHelper, DBInfo.TableUser.ISLOGGEDIN, "0",
                        DBInfo.TableUser.USERNAME, username);

                startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
