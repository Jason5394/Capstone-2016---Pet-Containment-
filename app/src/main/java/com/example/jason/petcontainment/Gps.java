package capstone;
import java.lang.Math;
import java.util.ArrayList;

public class Gps {
    private class Point{
        public Point(int x, int y) {
            // TODO Auto-generated constructor stub
            this.x = x;
            this.y = y;
        }
        int x;
        int y;
    }
    private int INF = 10000;

    public boolean OnSegment(Point p, Point q, Point r)
    {
        if(q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x)
                && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)){
            return true;
        }
        else{
            return false;
        }
    }

    public int orientation(Point p, Point q, Point r){
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);
        if(val == 0){
            return 0;
        }else{
            return (val > 0)? 1: 2;
        }
    }

    public boolean doIntersect(Point p1, Point q1, Point p2, Point q2){
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if(o1 != o2 && o3 != o4){
            return true;
        }

        if(o1 == 0 && OnSegment(p1, p2, q1)){
            return true;
        }
        if(o2 == 0 && OnSegment(p1, q2, q1)){
            return true;
        }
        if(o3 == 0 && OnSegment(p1, p1, q2)){
            return true;
        }
        if(o4 == 0 && OnSegment(p2, q1, q2)){
            return true;
        }
        return false;
    }

    public boolean isInside(Point polygon[], int n, Point p){
        if(n < 3){
            return false;
        }
        Point extreme = new Point(INF, p.y);
        int count = 0, i = 0;
        do{
            int next = (i + 1)%n;

            if(doIntersect(polygon[i], polygon[next], p, extreme)){
                if((orientation(polygon[i], p, polygon[next])) == 0){
                    return OnSegment(polygon[i], p, polygon[next]);
                }
                count++;
            }
            i = next;
        }while(i != 0);
        return (count%2 == 1);
    }

    public int main(){
        Point[] polygon1s = null;
        polygon1s[0] = new Point(0, 0);
        polygon1s[1] = new Point(10, 0);
        polygon1s[2] = new Point(10, 10);
        polygon1s[3] = new Point(0, 10);

        int n = 4;
        Point p = new Point(20, 20);
        if(isInside(polygon1s, n, p)){
            System.out.println("Yes! \n");
        }else{
            System.out.println("No! \n");
        }
        return 0;
    }
}


