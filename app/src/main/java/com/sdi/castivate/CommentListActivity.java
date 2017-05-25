package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sdi.castivate.adapter.CommentAdapter;
import com.sdi.castivate.model.CommentModal;
import com.sdi.castivate.model.CommentsInput;
import com.sdi.castivate.model.CommentsOutput;
import com.sdi.castivate.model.MatchedCastingInput;
import com.sdi.castivate.model.MatchedCastingOutput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentListActivity extends Activity {

    LinearLayout layBack;
    ListView commentListView;
    ProgressDialog pDialog;
    Context context;
    List<CommentModal> commentList;
    int talentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        context = this;
        layBack = (LinearLayout) findViewById(R.id.layBack);
        commentListView = (ListView) findViewById(R.id.commentList);
        commentList = new ArrayList<>();

        talentId = getIntent().getIntExtra("talentid", 0);
        comments(talentId);

        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void comments(int talentId) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        CommentsInput input = new CommentsInput(talentId + "");
        RegisterRemoteApi.getInstance().setCommentsInput(input);

        System.out.println("comments service calling==============");
        RegisterRemoteApi.getInstance().comments(context, new Callback<CommentsOutput>() {
            @Override
            public void success(CommentsOutput commentsOutput, Response response) {
                System.out.println("comments service response   :" + commentsOutput.Comments.toString());
                commentListView.setAdapter(new CommentAdapter(context, commentsOutput.Comments));
                closeProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
            }
        });
    }

    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}