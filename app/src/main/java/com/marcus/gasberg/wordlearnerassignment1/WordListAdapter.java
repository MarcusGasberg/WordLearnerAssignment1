package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<Word> wordsCache;

    WordListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View wordView = layoutInflater.inflate(R.layout.word_view_item, parent, false);
        return new WordViewHolder(wordView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        if(wordsCache != null){
            Word currentWord = wordsCache.get(position);
            holder.bind(currentWord);
        }
    }

    @Override
    public int getItemCount() {
        return wordsCache != null ? wordsCache.size() : 0;
    }

    void setWords(List<Word> words){
        wordsCache = words;
        notifyDataSetChanged();
    }

    class WordViewHolder extends RecyclerView.ViewHolder{
        private TextView wordNameTxt;
        private TextView pronunciationTxt;
        private ImageView wordImage;
        private TextView scoreTxt;
        private CardView card;

        WordViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);

                    Word wordSelected = wordsCache.get(getAdapterPosition());
                    intent.putExtra("id", wordSelected.Id);

                    context.startActivity(intent);
                }
            });
        }

        void bind(Word word){
            wordNameTxt.setText(word.Name);
            pronunciationTxt.setText(word.Pronunciation);
            scoreTxt.setText(String.valueOf(word.Rating));

            String path = ImageHelpers.getAnimalPath(word.Name);
            Bitmap bmp = ImageHelpers.getBitmapFromAssets(context, path);
            if(bmp != null){
                wordImage.setImageBitmap(bmp);
            }
    }

        private void initView(){
            wordNameTxt = itemView.findViewById(R.id.name_txt);
            pronunciationTxt = itemView.findViewById(R.id.pronunciation_txt);
            scoreTxt = itemView.findViewById(R.id.score_txt);
            wordImage = itemView.findViewById(R.id.word_image);
            card = itemView.findViewById(R.id.word_card);
        }
    }
}
