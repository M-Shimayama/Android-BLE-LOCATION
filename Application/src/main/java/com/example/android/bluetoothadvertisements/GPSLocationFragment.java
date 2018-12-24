package com.example.android.bluetoothadvertisements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * GPSの位置情報の更新を管理するためのビュー.
 * スイッチを入れてる間はGPSの座標を常に取り続ける.
 */
public class GPSLocationFragment extends Fragment implements View.OnClickListener  {
    private static String TAG = GPSLocationFragment.class.getSimpleName();

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;



    public GPSLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static GPSLocationFragment newInstance() {
        GPSLocationFragment fragment = new GPSLocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gpslocation, container, false);
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * switchのクリックをハンドリングする
     * */
    @Override
    public void onClick(View view) {

    }

    /**
     * Receiver for broadcasts sent by {@link GPSLocationService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(GPSLocationService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(getActivity(), Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
