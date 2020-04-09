package com.example.internetcookbook.fragmentpresenter

import android.view.View
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class HomeFragPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    override fun doSendComment(
        comment: String,
        dataModel: DataModel
    ) {
        doAsync {
            infoStore!!.sendComment(comment, dataModel)
            onComplete {
                view.commentAdded()
            }
        }
    }

    fun doRefreshData(swipeView: View) {
        doAsync {
            infoStore!!.getPostData()
            onComplete {
                doFindHomeData()
                swipeView.swipeToRefresh.isRefreshing = false
                view.initScrollListener()
            }
        }
    }

    fun doFilterDifficulty(difficultyLevel: String) {
        doAsync {
            infoStore!!.getFilterDataDifficulty(difficultyLevel)
            onComplete {
                view.showInformation(infoStore!!.getFilteredData())
            }
        }
    }

    fun doFilterTop(){
        doAsync {
            infoStore!!.getFilterDataTop()
            onComplete {
                view.showInformation(infoStore!!.getFilteredData())
            }
        }
    }


    fun loadMoreData() {
        var moreDataAvilable = false
        doAsync {
            moreDataAvilable = infoStore!!.getMoreData()
            onComplete {
                if (moreDataAvilable) {
                    view.initScrollListener()
                    view.removeLoading(findData())
                }else{
                    view.noDataAvilable()
                }
            }
        }
    }

    fun findIngredientsSearch(): ArrayList<DataModel?> {
        return infoStore!!.getFilteredData()
    }

    fun findData(): ArrayList<DataModel?> {
        return infoStore!!.getHomeData()
    }

    fun doFindHomeData(){
        view.showInformation(infoStore!!.getHomeData())
    }

    fun doAddBasket(dataModel: DataModel) {
        doAsync {
            infoStore!!.basketAdd(dataModel)
            onComplete {
                doAsync {
                    infoStore!!.getBasketData()
                }
            }
        }
    }

    fun doFindCurrentUser(): UserMasterModel {
        return infoStore!!.getCurrentUser()
    }

    override fun doHeartData(id: String) {
        doAsync {
            infoStore!!.putHeart(id)
        }
        doAsync {
            infoStore!!.doUpdateUserHeart(id)
        }
    }

    fun doRemoveHeart(id: String){
        doAsync {
            infoStore!!.removeHeart(id)
        }
    }
}