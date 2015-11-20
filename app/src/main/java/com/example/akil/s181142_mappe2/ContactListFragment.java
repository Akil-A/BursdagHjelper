package com.example.akil.s181142_mappe2;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

public class ContactListFragment extends ListFragment  {

    private ArrayList<Contact> mContactsList;
    private DBHandler mDBHandler;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDBHandler = new DBHandler(getActivity());
        mContactsList = mDBHandler.getContacts();
        ArrayAdapter arrayAdapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, mContactsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(mContactsList.get(position).getmFirstName() + " " + mContactsList.get(position).getmLastName());
                text2.setText(DateFormat.getDateInstance(DateFormat.LONG).format(mContactsList.get(position).getmBirthday().getTime()));
                return view;
            }
        };
        setListAdapter(arrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AddContactActivity.class);
                Contact contact = (Contact) parent.getAdapter().getItem(position);
                intent.putExtra("id", contact.get_ID());
                startActivity(intent);
            }
        });
    }
}
