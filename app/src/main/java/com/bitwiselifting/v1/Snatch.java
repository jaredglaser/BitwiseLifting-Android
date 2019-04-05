package com.bitwiselifting.v1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Snatch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Snatch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Snatch extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snatch, container, false);
        Button startBtn = view.findViewById(R.id.snatchStartButton);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Orientation.class);
                startActivityForResult(myIntent, 0);

            }
        });
        return view;
    }


}
