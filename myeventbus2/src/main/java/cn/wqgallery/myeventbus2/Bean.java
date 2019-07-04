package cn.wqgallery.myeventbus2;

public class Bean {
    private String name;
    private int age;

    public Bean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
