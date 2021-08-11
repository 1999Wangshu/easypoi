package com.example.easypoi.stream;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.easypoi.pojo.UserTest;
import com.example.easypoi.service.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest
public class StreamMapDecimal {

    @Autowired
    private CustomerService customerService;
    List<UserTest> list = null;

    @Before
    public void before(){
        list = new ArrayList<>();
        BigDecimal ps = new BigDecimal("111");

        list.add(new UserTest(1,"liubei",new BigDecimal("111"),new BigDecimal("30"),186));
        list.add(new UserTest(2,"zhangfei",new BigDecimal("222"),new BigDecimal("35"),185));
        list.add(new UserTest(3,"guanyu",new BigDecimal("333"),new BigDecimal("29"),179));
    }

    @Test
    public void test() {


        System.out.println("\ntest1:");
        list.stream().map(n->n)
                .forEach(n-> System.out.println(n));

        System.out.println("\ntest2--age:");
        list.stream().map(n->n.getAge())
                .forEach(System.out::println);

        System.out.println("\ntest3--age:");
        list.stream().map(UserTest::getAge)
                .map(n->n).forEach(n-> System.out.println(n));

        System.out.println("\ntest2--username:");
        list.stream().map(n->n.getUsername())
                .forEach(n-> System.out.println(n));

        System.out.println("\ntest3--username:");
        list.stream().map(n->n.getUsername()).map(n->n).forEach(n-> System.out.println(n));

        System.out.println("\ntest4--mapCopy");
        List<UserTest> collect = list.stream().map(dat -> {
            UserTest userTest = new UserTest();
            BeanUtils.copyProperties(dat, userTest);
            return userTest;
        }).collect(Collectors.toList());
        collect.stream().map(n->n).forEach(System.out::println);

    }

    @Test
    public void testStreamReduce(){
        //map用来归类，结果一般是一组数据，比如可以将list中的学生分数映射到一个新的stream中。
        //reduce用来计算值，结果是一个值，比如计算最高分。

        //相当于sum求和
        BigDecimal reduce = list.stream().map(UserTest::getPassword).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println(reduce);

        Integer sum = list.stream().map(UserTest::getHeight).reduce(0, Integer::sum);
        System.out.println(sum);

        //对List进行降序排序
        list.sort(new Comparator<UserTest>(){
            @Override
            public int compare(UserTest s1, UserTest s2) {
                //降序排序
                return Integer.compare(s2.getAge().intValue(), s1.getAge().intValue());
            }
        });

        System.out.println("list = " + list);
    }

    @Test
    public void testStreamFilter(){

        //过滤,列出30以上的并降序
        List<UserTest> list1 = list.stream()
                .filter(s -> s.getAge().intValue() >= 30)
                //.sorted((s1,s2)-> Integer.compare(s2.getAge().intValue(),s1.getAge().intValue()))
                //使用Comparator中的comparing方法
                .sorted(Comparator.comparing(UserTest::getAge).reversed())
                .collect(Collectors.toList());
        System.out.println(list1);

        //正则过滤
        Pattern pattern = Pattern.compile(".*a+.*");
        List<UserTest> collect = list.stream().filter(s -> pattern.matcher(s.getUsername()).matches()).collect(Collectors.toList());
        System.out.println(collect);

        //包含‘a’
        boolean b = Pattern.matches(".*a+.*", "vsvfsacd");
        System.out.println(b);
    }

    @Test
    public void testMapFun(){

        //使用map方法获取list数据中的name
        List<String> names = list.stream().map(UserTest::getUsername).collect(Collectors.toList());
        System.out.println(names);

        //使用map方法获取list数据中的name的长度
        List<Integer> length = list.stream().map(UserTest::getUsername).map(String::length).collect(Collectors.toList());
        System.out.println(length);

        //将每人的年龄-10
        List<Integer> score = list.stream().map(UserTest::getAge).map(i -> i.intValue() - 10).collect(Collectors.toList());
        System.out.println(score);

        // 获取总数
        double  ps = list.stream().mapToDouble(n->n.getPassword().intValue()).sum();
        System.out.println(ps);

        //计算总数
        BigDecimal sum = list.stream().map(UserTest::getPassword).reduce(new BigDecimal("0"), (a, b) -> BigDecimal.valueOf(a.intValue() + b.intValue()));
        System.out.println(sum);

        //计算总升高，返回Optional类型的数据，该类型是java8中新增的，主要用来避免空指针异常
        Optional<Integer> totalScore2 = list.stream().map(UserTest::getHeight).reduce((a, b) -> a + b);
        System.out.println(totalScore2.get());

        //计算最高和最低
        Optional<Integer> max = list.stream().map(UserTest::getHeight).reduce(Integer::max);
        Optional<Integer> min = list.stream().map(UserTest::getHeight).reduce(Integer::min);

        System.out.println(max.get());
        System.out.println(min.get());
    }

    @Test
    public void streamFun2(){
        //定义一个100元素的集合，包含A-Z
        List<String> list = new LinkedList<>();
        for (int i =0;i<100;i++){
            list.add(String.valueOf((char)('A'+Math.random()*('Z'-'A'+1))));
        }
        System.out.println(list);
        //统计集合重复元素出现次数，并且去重返回hashmap
        Map<String, Long> map = list.stream().
                collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println(map);
        //由于hashmap无序，所以在排序放入LinkedHashMap里(key升序)
        Map<String, Long> sortMap = new LinkedHashMap<>();
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).
                forEachOrdered(e -> sortMap.put(e.getKey(), e.getValue()));
        System.out.println(sortMap);
        //获取排序后map的key集合
        List<String> keys = new LinkedList<>();
        sortMap.entrySet().stream().forEachOrdered(e -> keys.add(e.getKey()));
        System.out.println(keys);
        //获取排序后map的value集合
        List<Long> values = new LinkedList<>();
        sortMap.entrySet().stream().forEachOrdered(e -> values.add(e.getValue()));
        System.out.println(values);

    }

    @Test
    public void testJiSuan(){

        BigDecimal b = new BigDecimal("1060");
        BigDecimal b2 = new BigDecimal("369");

        //BigDecimal.ROUND_DOWN  ROUND_UP向上/向下取整
        BigDecimal d = b2.divide(b, 2,BigDecimal.ROUND_HALF_UP);
        System.out.println(d);
    }


    @Test
    public void testmapToLong(){
        //数值流，在java8中新增了三个原始类型流（IntStream、DoubleStream、LongStream）来解决求和，求平均

        OptionalDouble avg = list.stream().mapToInt(UserTest::getHeight).average();
        System.out.println(avg.getAsDouble());

        int sum = list.stream().mapToInt(UserTest::getHeight).sum();
        System.out.println(sum);

        //生成1-100的数
        IntStream num = IntStream.rangeClosed(1, 100);
//        List collect = num.mapToLong(n->n).collect(Collectors.toList());
        long[] longs = num.mapToLong(n -> n).toArray();
        System.out.println(Arrays.toString(longs));

        long count = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0).count();
        System.out.println(count);

        int[] ints = IntStream.range(0, 5).map(x -> x * 2).toArray();
        System.out.println(Arrays.toString(ints));
    }


    //创建流
    @Test
    public void createStream(){
        Stream str = Stream.of("i","love","this","game");
        str.limit(2).forEach(System.out::println);

        //使用数组创建流
        int[] num = {2,5,9,8,6};
        IntStream intStream = Arrays.stream(num);
        int sum = intStream.sum();//求和
        System.out.println(sum+"===================");

        //由函数生成流，创建无限流
        Stream.iterate(0, n -> n+2).limit(10).forEach(System.out::println);

    }

    //去重
    public void distinct(){

        List<UserTest> collect = list.stream().filter(distinctByKey(d -> d.getUsername())).collect(Collectors.toList());
        for (UserTest re : collect) {
            System.out.println(re);
        }

        ArrayList<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        //2.
        list1.stream().distinct().collect(Collectors.toList());
    }
    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    //集合装JSON,转map
    @Test
    public void testJSON(){

        String s = JSONObject.toJSONString(list);
        System.out.println(s);
        System.out.println(list.toString());

        Map<Integer, String> map = list.stream().collect(Collectors.toMap(UserTest::getId, UserTest::getUsername));
        String s1 = JSONObject.toJSONString(map);
        System.out.println(s1);

        Map<Integer, UserTest> collect = list.stream().collect(Collectors.toMap(UserTest::getId, dat -> dat));
        //account -> account是一个返回本身的lambda表达式，其实还可以使用Function接口中的一个默认方法代替，使整个方法更简洁优雅
        Map<Integer, UserTest> collect1 = list.stream().collect(Collectors.toMap(UserTest::getId, Function.identity()));
        //上面方法重复key的情况下可能报错（java.lang.IllegalStateException: Duplicate key）
        //可以传入一个合并的函数来解决key冲突问题
        Map<Integer, UserTest> collect2 = list.stream().collect(Collectors.toMap(UserTest::getId, Function.identity(), (key1, key2) -> key2));
        
        //只是简单的使用后者覆盖前者来解决key重复问题。还有一种分组的方法
        //注意：如果Collectors.groupingBy的key未null,抛出空指针
        Map<Integer, List<UserTest>> mapList = list.stream().collect(Collectors.groupingBy(UserTest::getId));
        //指定一个Map的具体实现，来收集数据
        list.stream().collect(Collectors.toMap(UserTest::getUsername, Function.identity(), (key1, key2) -> key2, LinkedHashMap::new));

    }

    @Test
    public void testNull(){

        List<UserTest> collect = null;
        if (ObjectUtil.isNotEmpty(collect))
        for (UserTest userTest : collect) {
            System.out.println(userTest);
        }

        if (ObjectUtil.isNotEmpty(collect)) {
            List<UserTest> collect1 = collect.stream().filter(dat -> dat.getUsername() != null).collect(Collectors.toList());
        }

        long count = list.stream().filter(ObjectUtil::isNotEmpty).filter(dat -> dat.getHeight() >= 185).count();

        System.out.println(count);
        Map<String, List<UserTest>> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(list)){
            map = list.stream().filter(dat -> dat.getId() != null).collect(Collectors.groupingBy(UserTest::getUsername));
        }

        if (ObjectUtil.isNotEmpty(map)){
            for (Map.Entry<String, List<UserTest>> e : map.entrySet()) {
                System.out.println(e.getKey()+",,,"+e.getValue());
            }
        }



    }

    @Test
    public void str(){

        String s = "https://";

        if (s.contains("blob:")){
            String replace = s.replace("blob:", "");
            System.out.println(replace);
        }

        String replace = s.replace(",", "|");
        System.out.println(replace);

    }


}
