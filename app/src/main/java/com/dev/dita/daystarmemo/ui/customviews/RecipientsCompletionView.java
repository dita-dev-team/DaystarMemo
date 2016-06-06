package com.dev.dita.daystarmemo.ui.customviews;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.objects.Recipient;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * The type Recipients completion view.
 * This class handles the creation of tokens in the MultiAutoCompleteView
 */
public class RecipientsCompletionView extends TokenCompleteTextView<Recipient> {
    public RecipientsCompletionView(Context context) {
        super(context);
    }

    public RecipientsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecipientsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Recipient recipient) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.recipient_token, (ViewGroup) RecipientsCompletionView.this.getParent(), false);
        ((TextView) view.findViewById(R.id.recipient_name)).setText(recipient.username);

        return view;
    }

    @Override
    protected Recipient defaultObject(String completionText) {
        return null;
    }
}
