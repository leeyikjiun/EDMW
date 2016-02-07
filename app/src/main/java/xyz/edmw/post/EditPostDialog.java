package xyz.edmw.post;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.rest.RestClient;
import xyz.edmw.thread.ReplyForm;
import xyz.edmw.thread.ThreadActivity;

public class EditPostDialog implements View.OnClickListener {
    private final AlertDialog.Builder builder;
    private final EditText editText;
    private final Context context;
    private final String postId;
    private AlertDialog dialog;

    public EditPostDialog(Context context, String postId) {
        this.context = context;
        this.postId = postId;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        editText = new EditText(context);
        builder.setView(editText);
    }

    public void show() {
        dialog = builder.show();
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(this);
    }

    public void setText(String text) {
        editText.setText(text);
    }

    @Override
    public void onClick(final View v) {
        String message = editText.getText().toString().trim();
        message = message.replace(System.getProperty("line.separator"), "<br />");

        // prevent people from sending multiple times when network is slow
        v.setEnabled(false);
        Toast.makeText(context, "Editing...", Toast.LENGTH_SHORT).show();

        final ThreadActivity activity = (ThreadActivity) context;
        ReplyForm replyForm = activity.getReplyForm();
        Call<Void> call = RestClient.getService().edit(
                replyForm.getSecurityToken(),
                postId,
                replyForm.getParentId(),
                message
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    editText.setText("");
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Failed to edit post", Toast.LENGTH_SHORT).show();
                }
                v.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(activity, "Failed to edit post", Toast.LENGTH_SHORT).show();
                v.setEnabled(true);
            }
        });
    }
}