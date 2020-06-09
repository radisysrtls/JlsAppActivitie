/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view.fragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Util;


public class InviteFragment extends Fragment {

    private CardView cardInstruction;
    private ImageView instructionIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_invite, container, false);
         initUI(view);
        return view;
    }

    private void initUI(View view) {
        cardInstruction = view.findViewById(R.id.instruction_card);
        TextView instruction1 = view.findViewById(R.id.device_instruction1);
        instruction1.setTypeface(Util.mTypeface(getActivity(),5));
        TextView instruction2 = view.findViewById(R.id.device_instruction2);
        instruction2.setTypeface(Util.mTypeface(getActivity(),3));
        instructionIcon = view.findViewById(R.id.devices_default_icon);
        cardInstruction.setVisibility(View.VISIBLE);
        instructionIcon.setVisibility(View.VISIBLE);

    }
}