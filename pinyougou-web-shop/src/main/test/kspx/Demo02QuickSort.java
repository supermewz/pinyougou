package kspx;

/**
 * 快速排序
 */
public class Demo02QuickSort {
    public static void main(String[] args) {
        int arr[]={5,4,6,8,3,9,2,0,1,7};
        quickSort(arr,0 ,arr.length-1 );
        for (int i : arr) {
            System.out.print(i+" ");
        }
    }
    public static void quickSort(int[] arr,int left,int rigth){
        //进行判断
        if(left>rigth){
            return;
        }
        int base = arr[left];
        //定义变量I
        int i = left;
        int j = rigth;

        while (i!=j){
//            先由J从右往左检索比基准数小的 如果检索到比基准数小的就停下
            while (arr[j]>=base && i<j){
                j--;
            }
            while (arr[i]<= base && i<j){
                i++;
            }

            int temp  = arr[i];
            arr[i]=arr[j];
            arr[j]=temp;
        }
        //如果while循环的条件不成立  就会跳出这个循环

        //如果这个条件不成立说明两数相遇了

        //如果i和j 相遇了  那就交换基准书这个元素和相遇位置的元素
        arr[left] = arr[i];
        //把基准数赋值给相遇位置的元素
        arr[i] = base;
        quickSort(arr,left ,i-1 );
        quickSort(arr,j+1 ,rigth );
    }
}
