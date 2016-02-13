package xyz.edmw.thread;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class GotoPageDialog implements View.OnClickListener {
    @Bind(R.id.goto_page_num)
    EditText pageNum;
    @Bind(R.id.goto_num_pages)
    TextView txtNumPages;

    private final Context context;
    private final AlertDialog.Builder builder;
    private final int numPages;
    private final OnPageSelectedListener listener;
    private AlertDialog dialog;

    public GotoPageDialog(Context context, int numPages, OnPageSelectedListener listener) {
        this.context = context;
        this.numPages = numPages;
        this.listener = listener;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Go to page");
        builder.setPositiveButton("Go", null);
        builder.setNegativeButton("Cancel", null);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_goto_page, null);
        ButterKnife.bind(this, view);
        builder.setView(view);

        this.txtNumPages.setText("/ " + numPages);
        pageNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                Log.d("id", String.valueOf(id));
                if (id == R.id.go || id == EditorInfo.IME_ACTION_GO) {
                    Log.d("lol", "asd");
                    attemptGotoPage();
                    return true;
                }
                return false;
            }
        });
    }

    public void show() {
        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
            attemptGotoPage();
    }

    private void attemptGotoPage() {
        this.pageNum.setError(null);

        String pageNumStr = this.pageNum.getText().toString();
        if (TextUtils.isEmpty(pageNumStr)) {
            this.pageNum.setError(context.getString(R.string.error_field_required));
            this.pageNum.requestFocus();
            return;
        }

        int pageNum = Integer.parseInt(pageNumStr);
        if (pageNum <= 0 || pageNum > numPages) {
            this.pageNum.setError(pageNumStr + " is not a valid page");
            this.pageNum.requestFocus();
        } else {
            listener.onPageSelected(pageNum);
            dialog.dismiss();
        }
    }

    public interface OnPageSelectedListener {
        void onPageSelected(int pageNum);
    }
}