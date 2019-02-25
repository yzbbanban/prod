package com.pl.prod.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.entity.Product;
import com.pl.prod.entity.WifiParam;


/**
 * Created by brander on 2017/11/20.
 * 产品适配器
 */

public class WifiListAdapter extends BaseRecyclerViewAdapter<WifiParam> {


    private ButtonInterface buttonInterface;
    private WifiParam wifiParam;


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


    public WifiListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, parent, false);
        WifiViewHolder wifiViewHolder = new WifiViewHolder(view);
        return wifiViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        wifiParam = mList.get(position);
        WifiViewHolder wifiViewHolder = (WifiViewHolder) holder;
        wifiViewHolder.tvWifiName.setText(wifiParam.getWifiName());
        wifiViewHolder.btnWifiMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonInterface != null) {
//                  接口实例化后的而对象，调用重写后的方法
                    buttonInterface.onclick(v, position);
                }

            }
        });
    }

    class WifiViewHolder extends BaseViewHolder {
        TextView tvWifiName;
        Button btnWifiMore;

        public WifiViewHolder(View itemView) {
            super(itemView);
            tvWifiName = itemView.findViewById(R.id.tv_wifi_name);
            btnWifiMore = itemView.findViewById(R.id.btn_wifi_more);

        }
    }

}
