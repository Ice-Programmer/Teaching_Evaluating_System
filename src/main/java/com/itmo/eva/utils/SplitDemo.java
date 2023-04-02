package com.itmo.eva.utils;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiaoxu
 * @date 2023-01-29
 * java_demo:com.xiaoxu.test.other.SplitDemo
 */
public class SplitDemo {

    /**
     * @param value 需要拆分的数值
     * @param splitCount 总共拆分为splitCount个非负数，拆分的全部非负数之和，等于需要拆分的数值value（拆分数中可能含有0）
     */
    public static List<Long> demo(long value, int splitCount){
        if(value <= 0 || splitCount <= 0){
            throw new RuntimeException("should not be 0");
        }

        List<Long> splitArr = Lists.newArrayList();

        if(splitCount == 1){
            splitArr.add(value);
            return splitArr;
        }

        int exeCount = 0;

        Random rand = new Random();
        List<Double> randRange;
        int var1;

        /*
        * 规避随机数中含相同数的情况，实际概率极小
        * */
        do{
            var1 = splitCount - 1;
            randRange = Lists.newArrayList();

            do{
                randRange.add(rand.nextDouble());
            }while(--var1 > 0);

            exeCount ++;

            if(exeCount > 3){
                throw new RuntimeException("repeat execute fail!!");
            }

        }while(randRange.stream().distinct().count() != randRange.size());

        /*
        * 顺序排序,由小到大(避免中间变量相减后出现为负数的情况)
        * */
        randRange.sort(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1 < o2 ? -1 : 1;
            }
        });

        AtomicInteger atmInt = new AtomicInteger();

        long var2 = 0;

        do{

            if(atmInt.get() == splitCount - 1){
                splitArr.add(value - var2);
                break;
            }

            long var3 = (long)(randRange.get(atmInt.getAndIncrement()) * value);
            long var4 = var3 - var2;
            splitArr.add(var4);
            var2 = var3;

        }while(atmInt.get() < splitCount);

        return splitArr;
    }

    public static void main(String[] args) {
        List<Long> demoArr = demo(133, 6);
//        List<Long> demoArr = demo(5, 6);
        System.out.println("arr:" + demoArr);
        System.out.println("total:" + demoArr.stream().reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException("wrong")));
    }

}
