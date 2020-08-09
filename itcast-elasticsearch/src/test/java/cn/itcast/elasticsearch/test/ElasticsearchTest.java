package cn.itcast.elasticsearch.test;

import cn.itcast.elasticsearch.ItemRepository;
import cn.itcast.elasticsearch.pojo.Item;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testIndex(){
        this.elasticsearchTemplate.createIndex(Item.class);
        this.elasticsearchTemplate.putMapping(Item.class);
    }

    @Test
    public void testCreate(){
        // 新增一条数据
        /*Item item = new Item(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        this.itemRepository.save(item);*/

        // 批量新增数据
        /*List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(3L, "华为META10", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        this.itemRepository.saveAll(list);*/

        // 更新数据，只要id一样，自动覆盖
        Item item = new Item(6L, "apple手机", "手机",
                "apple", 3599.00, "http://image.leyou.com/13123.jpg");
        this.itemRepository.save(item);
    }

    @Test
    public void testFind(){
        // 根据id查询数据
        /*Optional<Item> item = this.itemRepository.findById(1L);
        System.out.println(item.get());*/
        // 查询所有
        Iterable<Item> items = this.itemRepository.findAll(Sort.by("price").descending());
        items.forEach(System.out::println);
    }

    @Test
    public void findByTitle(){
        List<Item> phones = this.itemRepository.findByTitle("手机");
        phones.forEach(System.out::println);
    }

    @Test
    public void findByPriceBetween(){
        List<Item> phones = this.itemRepository.findByPriceBetween(3699d, 4499d);
        phones.forEach(System.out::println);
    }

    @Test
    public void indexList() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }

    @Test
    public void testSearch(){
        /*// 通过查询构建器工具构建查询条件
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "手机");
        // 执行查询，获取结果集
        Iterable<Item> items = this.itemRepository.search(queryBuilder);
        items.forEach(System.out::println);*/

        // 模糊查询
        FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("title", "appla");
        Iterable<Item> items = this.itemRepository.search(queryBuilder);
        items.forEach(System.out::println);
    }

    @Test
    public void testNative(){
        // 构建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "手机"));
        // 执行查询获取分页结果集，不分页的话默认是一页
        Page<Item> itemPage = this.itemRepository.search(queryBuilder.build());
        System.out.println(itemPage.getTotalPages());
        System.out.println(itemPage.getTotalElements());
        itemPage.getContent().forEach(System.out::println);
    }

    @Test
    public void testPage(){
        // 构建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("category", "手机"));
        // 添加分页条件, 页码是从0开始的
        // queryBuilder.withPageable(PageRequest.of(1, 2));
        // 添加排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        // 执行查询获取分页结果集，不分页的话默认是一页
        Page<Item> itemPage = this.itemRepository.search(queryBuilder.build());
        System.out.println(itemPage.getTotalPages());
        System.out.println(itemPage.getTotalElements());
        itemPage.getContent().forEach(System.out::println);
    }
}

















