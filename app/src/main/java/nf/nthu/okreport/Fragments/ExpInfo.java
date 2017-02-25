package nf.nthu.okreport.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import nf.nthu.okreport.R;
import nf.nthu.okreport.Report;
import nf.nthu.okreport.utils.MyScrollView;

public class ExpInfo extends Fragment {
    public static MyScrollView scview;

    public ExpInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exp_info, container, false);
        TextView exp_info = (TextView)view.findViewById(R.id.exp_info);
        exp_info.setText(Report.Exp_Report);
        scview = (MyScrollView)view.findViewById(R.id.scview);
        return view;
    }

}
