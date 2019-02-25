package com.pl.prod.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.entity.Product;
import com.pl.prod.ui.SmartImageView;


/**
 * Created by brander on 2017/11/20.
 * 产品适配器
 */

public class ProductListAdapter extends BaseRecyclerViewAdapter<Product> {


    private ButtonInterface buttonInterface;
    private ButtonIvInterface buttonIvInterface;
    private Product product;


    /**
     * 点击到达事件需要的方法
     */
    public void buttonSetOnclick(ButtonInterface buttonInterface) {
        this.buttonInterface = buttonInterface;
    }

    /**
     * 按钮点击到达事件对应的接口
     */
    public interface ButtonInterface {

        void onclick(View view, int position);
    }

    /**
     * 按钮点击到达事件对应的接口
     */
    public interface ButtonIvInterface {

        void onclick(View view, int position);
    }


    /**
     * 点击到达事件需要的方法
     */
    public void buttonIvSetOnclick(ButtonIvInterface buttonIvInterface) {
        this.buttonIvInterface = buttonIvInterface;
    }



    public ProductListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        product = mList.get(position);
        ProductViewHolder productViewHolder = (ProductViewHolder) holder;
        productViewHolder.tvProductId.setText(product.getSsid());
//        productViewHolder.ivProdSh.setImageResource(R.drawable.logo);
        productViewHolder.ivProdSh.setImageUrl("http://www.uuabb.cn/qq.png");
        productViewHolder.ivProdSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonIvInterface != null) {
//                  接口实例化后的而对象，调用重写后的方法
                    buttonIvInterface.onclick(v, position);
                }
            }
        });
//        productViewHolder.btnProdMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (buttonInterface != null) {
////                  接口实例化后的而对象，调用重写后的方法
//                    buttonInterface.onclick(v, position);
//                }
//
//            }
//        });
    }

    class ProductViewHolder extends BaseViewHolder {
        TextView tvProductId;
        Button btnProdMore;
        SmartImageView ivProdSh;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvProductId = itemView.findViewById(R.id.tv_product_id);
//            btnProdMore = itemView.findViewById(R.id.btn_prod_more);
            ivProdSh = itemView.findViewById(R.id.iv_prod_sh);

        }
    }

}
