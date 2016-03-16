package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.meiqia.core.MQManager;
import com.meiqia.core.MQScheduleRule;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.model.GoodsModel;
import com.meiqia.ue.ec.ui.activity.DetailActivity;
import com.meiqia.ue.ec.ui.widget.Divider;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午6:37
 * 描述:
 */
public class BeforeFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mGoodsRv;
    private GoodsAdapter mGoodsAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_before);
        mGoodsRv = getViewById(R.id.rv_before_goods);
    }

    @Override
    protected void setListener() {
        mGoodsAdapter = new GoodsAdapter(mGoodsRv);
        mGoodsAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mGoodsRv.setLayoutManager(new LinearLayoutManager(mApp));
        mGoodsRv.addItemDecoration(new Divider(mApp));
        mGoodsRv.setAdapter(mGoodsAdapter);

        loadDatas();
    }

    private void loadDatas() {
        mApp.getEngine().loadBeforeGoods()
                .compose(this.<List<GoodsModel>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mActivity.showLoadingDialog(R.string.mq_data_is_loading);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GoodsModel>>() {
                    @Override
                    public void call(List<GoodsModel> goodsModels) {
                        mActivity.dismissLoadingDialog();

                        mGoodsAdapter.setDatas(goodsModels);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.dismissLoadingDialog();

                        MQUtils.show(mApp, R.string.loading_data_failure);
                    }
                });
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        MQManager.getInstance(mApp).setScheduledAgentOrGroupWithId("990a7cbe603fe029e269b4c32f4fed09", "", MQScheduleRule.REDIRECT_GROUP);
        mActivity.forward(DetailActivity.class);
    }

    private static class GoodsAdapter extends BGARecyclerViewAdapter<GoodsModel> {

        public GoodsAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_before_goods);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, GoodsModel model) {
            helper.setText(R.id.tv_before_goods_title, model.title);
        }
    }
}
