package com.learn2crack.nfc;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.learn2crack.nfc.MainActivity.SERVER_IP;
import static com.learn2crack.nfc.MainActivity.SERVER_PORT;

public class NFCReadFragment extends DialogFragment {

    public static final String TAG = NFCReadFragment.class.getSimpleName();

    public static NFCReadFragment newInstance() {

        return new NFCReadFragment();
    }

    private TextView mTvMessage;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MainActivity)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef){

        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
//            message = message.substring(3);
            Log.e(TAG, "readFromNFC: "+message);
            mTvMessage.setText(message);

            sendMessageToServer(message);

            ndef.close();

        } catch (IOException | FormatException | NullPointerException e) {
            e.printStackTrace();

        }
    }

    private void sendMessageToServer(String tagId) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String url = "http://" + SERVER_IP + ":" + SERVER_PORT + "/figurine/" + tagId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    mTvMessage.setText(response.toString());
//                        mTextView.setText("Response: " + response.toString());
        },
                error -> {
            Log.e("error", Arrays.toString(error.getStackTrace()));
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "This tag is not registered", Toast.LENGTH_LONG).show());

        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "e4bbe5b7a4c1eb55652965aee885dd59bd2ee7f4");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
