package com.bauble_app.bauble;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by princ on 4/11/2017.
 */

public class MyDialogFragment extends DialogFragment {
    private int dialogLayoutId;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static MyDialogFragment newInstance(int dialogLayoutId) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply num input as an argument.
        // 0 = Forgot password dialog
        // 1 = Messages dialog
        Bundle args = new Bundle();
        args.putInt("dialogLayoutId", dialogLayoutId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogLayoutId = getArguments().getInt("dialogLayoutId");

        // Pick a style based on the num.
        /*
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((dialogLayoutId-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((dialogLayoutId-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(this.dialogLayoutId, container, false);

        // Watch for button clicks.
        /*Button button = (Button)v.findViewById(R.id.show);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                /*((FragmentDialog) getActivity()).showDialog();
            }
        });
        */

        return v;
    }
}
