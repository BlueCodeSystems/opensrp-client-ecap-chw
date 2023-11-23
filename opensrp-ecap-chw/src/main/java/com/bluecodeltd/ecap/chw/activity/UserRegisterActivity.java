package com.bluecodeltd.ecap.chw.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.UserRegisterModel;

import java.util.ArrayList;
import java.util.List;

public class UserRegisterActivity extends AppCompatActivity {
    public static final String TAG = "ListViewExample";

    Context context;
    private ListView listView;
    private Button conduct_register;
    String groupName, groupId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        this.listView = findViewById(R.id.listView);
        this.conduct_register = findViewById(R.id.conduct_register);

        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: " + position);
            CheckedTextView v = (CheckedTextView) view;
            boolean currentCheck = v.isChecked();
            UserRegisterModel user = (UserRegisterModel) listView.getItemAtPosition(position);
            user.setActive(!currentCheck);
        });

        // Initialize and set the adapter
        initListViewData();

        this.conduct_register.setOnClickListener(v ->
                printSelectedItems());
    }

    private void initListViewData() {
        // Example data for the UserRegisterModel
        List<UserRegisterModel> userList = new ArrayList<>();
        userList.add(new UserRegisterModel("User1", "Type1", true));
        userList.add(new UserRegisterModel("User2", "Type2", true));
        userList.add(new UserRegisterModel("User3", "Type3", true));
        // Add more users as needed

        // Create and set the adapter
        ArrayAdapter<UserRegisterModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, userList);
        listView.setAdapter(adapter);
    }
//private void initListViewData() {
//    // Retrieve the members for the group
//    List<MembersModel> listMembers = new ArrayList<>();
//    listMembers.addAll(WeGroupMembersDao.getWeGroupMembersByGroupId(groupId));
//
//    // Create and set the adapter
//    ArrayAdapter<MembersModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listMembers);
//    listView.setAdapter(adapter);
//}

    public void printSelectedItems() {
        SparseBooleanArray sp = listView.getCheckedItemPositions();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sp.size(); i++) {
            if (sp.valueAt(i)) {
                UserRegisterModel user = (UserRegisterModel) listView.getItemAtPosition(i);
                String s = user.getUserName();
                sb = sb.append(s).append(", ");
            }
        }
    }
}
