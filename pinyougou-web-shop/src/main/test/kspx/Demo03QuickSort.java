package kspx;

import org.junit.Test;


public class Demo03QuickSort {

    @Test
    public void show(){
        int arr[]={5,4,6,8,3,9,2,0,1,7};
        kp(arr,0 ,arr.length-1 );
        for (int i : arr) {
            System.out.print(i+" ");
        }
    }

    public void kp(int[] arr,int left,int rigth){
        if(left>rigth){
            return;
        }
        int base = arr[left];
        int i = left;
        int j = rigth;

        while (i!=j){
            while (arr[j]>=base && i<j){
                j--;
            }
            while (arr[i]<=base && i>j){
                i++;
            }
            int temp = arr[i];
            arr[i]=arr[j];
            arr[j]=temp;
        }
        arr[left]=arr[i];
        arr[i]=base;
        kp(arr,left , i-1);
        kp(arr, j+1,rigth );


    }
}
