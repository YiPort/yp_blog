package com.yiport.controller;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.ArticleDoc;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SearchQuery;
import com.yiport.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    SearchService searchService;

    @GetMapping("/searchArticle1")
    public ResponseResult searchArticle1(Integer pageNum, Integer pageSize,String searchKey){

        List<ArticleDoc> articleDocListVOS = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            articleDocListVOS.add(
//                    new ArticleDoc(Long.parseLong(i+1+""),
//                            "aaa"+i+searchKey,
//                    "bb擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦" +
//                            "洒点水大叔大婶大萨达手动阀你哈你不从白色也吃你" +
//                            "撒可抵扣胶擦说不出大家啊啥东西阿吉双打卡" +
//                            "萨达姆到拉萨对那些哈凯撒小马达了哈到喜啦" +
//                            "大法师怒好吃的哈陕西科技大深V显卡" +
//                            "阿西木金茂大萨达手动阀你哈你不从白色也吃你" +
//                            "撒可抵扣胶擦说不出大家啊啥东西阿吉双打卡" +
//                            "萨达姆到拉萨对那些哈凯撒小马达了哈到喜啦" +
//                            "大法师怒好吃的哈陕西科技大深V显卡" +
//                            "阿西木金茂大萨达手动阀你哈你不从白色也吃你" +
//                            "撒可抵扣胶擦说不出大家啊啥东西阿吉双打卡" +
//                            "萨达姆到拉萨对那些哈凯撒小马达了哈到喜啦" +
//                            "大法师怒好吃的哈陕西科技大深V显卡" +
//                            "阿西木金茂大厦离开打撒接地开关" +
//                            "擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦b"+i,"java"+i+searchKey,
//                    "https://"+searchKey,
//                            Long.parseLong(i+1+""),
//                            "2023-01-0"+(1+i)));
//
//        }

        PageVO pageVO = new PageVO(articleDocListVOS, 10L);

        return ResponseResult.okResult(pageVO);
    }

    /**
     * 搜索博客
     *
     * @return 博客列表
     */
    @GetMapping("/searchArticle")
    public ResponseResult searchArticle(SearchQuery searchQuery){

        return searchService.searchArticle(searchQuery);

    }

}
