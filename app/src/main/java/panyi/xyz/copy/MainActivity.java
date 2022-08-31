package panyi.xyz.copy;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import panyi.xyz.copy.library.OnSelectListener;
import panyi.xyz.copy.library.SelectableTextHelper;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTest;

    private SelectableTextHelper mSelectableTextHelper;
    private LinearLayout llRoot;
    private TextView tvTest;

    String data [] = new String[]{"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "bbbbbbbbbbbbbbbbbbbbbbb",
            "CCCCCCCCCCCCCCCCCCCCC",
            "DDDDDDDDDDDDDDDDDDDDDDD",
            "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "bbbbbbbbbbbbbbbbbbbbbbb",
            "CCCCCCCCCCCCCCCCCCCCC",
            "DDDDDDDDDDDDDDDDDDDDDDD",
            "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "bbbbbbbbbbbbbbbbbbbbbbb",
            "CCCCCCCCCCCCCCCCCCCCC",
            "DDDDDDDDDDDDDDDDDDDDDDD",
            "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "bbbbbbbbbbbbbbbbbbbbbbb",
            "CCCCCCCCCCCCCCCCCCCCC",
            "DDDDDDDDDDDDDDDDDDDDDDD",
            "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"};

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(new MyAdapter(this, R.layout.item_view));
    }

    public class MyAdapter extends ArrayAdapter {
        private int layoutId;

        public MyAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            layoutId = resource;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            ViewHolder viewHolder = null;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(layoutId, null);
                viewHolder = new ViewHolder();
                viewHolder.contentText = view.findViewById(R.id.content);
                viewHolder.itemRoot = view.findViewById(R.id.item_root);
                view.setTag(viewHolder); // 将ViewHolder存储在view中
            } else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag(); // 重新获取ViewHolder
            }

            viewHolder.contentText.setText(data[position]);

            ViewHolder finalViewHolder = viewHolder;
            viewHolder.itemRoot.setOnClickListener((v)->{
                SelectableTextHelper selectTextHelper = new SelectableTextHelper.Builder(finalViewHolder.contentText)
                .setSelectedColor(getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                .build();

                selectTextHelper.showSelectViewAll();
            });
            return view;
        }
    }

    public static class ViewHolder {
        View itemRoot;
        TextView contentText;

        SelectableTextHelper selectTextHelper;
    }

    private void initView() {
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
    }
}
