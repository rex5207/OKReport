package nf.nthu.okreport.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.json.JSONException;

import java.io.IOException;

import nf.nthu.okreport.R;
import nf.nthu.okreport.Report;

public class CharInfo extends Fragment {

    public CharInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_char_info, container, false);
        TextView char_info = (TextView)view.findViewById(R.id.char_info);
        char_info.setText(Report.CharInfo_Report);
        return view;

    }


}
