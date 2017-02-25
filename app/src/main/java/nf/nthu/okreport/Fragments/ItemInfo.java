package nf.nthu.okreport.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import nf.nthu.okreport.utils.MyScrollView;
import nf.nthu.okreport.R;
import nf.nthu.okreport.Report;


public class ItemInfo extends Fragment {

    public static MyScrollView scview;
    public ItemInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_info, container, false);
        TextView item_info = (TextView)view.findViewById(R.id.item_info);
        item_info.setText(Report.Item_Report);
        scview = (MyScrollView)view.findViewById(R.id.scview);
        return view;
    }
}
