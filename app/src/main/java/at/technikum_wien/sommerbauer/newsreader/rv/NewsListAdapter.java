package at.technikum_wien.sommerbauer.newsreader.rv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.technikum_wien.sommerbauer.newsreader.R;
import at.technikum_wien.sommerbauer.newsreader.data.NewsItem;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ItemViewHolder> {
  public interface ListItemClickListener {
    void onListItemClick(NewsItem clickedItem);
  }

  private List<NewsItem> mItems;
  private ListItemClickListener listItemClickListener;

  class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mNewsItemTitleTextView;

    ItemViewHolder(View itemView) {
      super(itemView);
      mNewsItemTitleTextView = itemView.findViewById(R.id.tv_news_item_title);
      mNewsItemTitleTextView.setOnClickListener(this);
    }

    void bind(int index) {
      mNewsItemTitleTextView.setText(mItems.get(index).getTitle());
    }

    @Override
    public void onClick(View v) {
      int clickedItemIndex = getAdapterPosition();
      listItemClickListener.onListItemClick(mItems.get(clickedItemIndex));
    }
  }

  public NewsListAdapter(List<NewsItem> items, ListItemClickListener clickListener) {
    mItems = items;
    listItemClickListener = clickListener;
  }

  public NewsListAdapter(ListItemClickListener clickListener) {
    listItemClickListener = clickListener;
  }

  @Override
  @NonNull
  public ItemViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    int layoutId = R.layout.news_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);

    View view = inflater.inflate(layoutId, parent, false);
    return new ItemViewHolder(view);
  }

  @Override
  public int getItemCount() {
    return (mItems == null) ? 0 : mItems.size();
  }

  @Override
  public void onBindViewHolder(ItemViewHolder holder, int position) {
    holder.bind(position);
  }

  public void swapItems(List<NewsItem> items) {
    mItems = items;
    notifyDataSetChanged();
  }
}
