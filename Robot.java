import java.lang.Math;

class Robot{
  private Position position;  // ロボットの現在位置
  private String name;        // ロボットの名前
  private double energy;      // 現在,ロボットに蓄積されているエネルギー

  // ロボットの状態を初期設定
  public Robot(Position position, String name){
    this.name = name;
    this.position = new Position(position.getX(), position.getY());
  }

  public Robot(String name, int x, int y){
    this.name = name;
    this.position = new Position(x, y);
  }
  // 2. エネルギーも受け取れるように修正
  public Robot(Position position, String name, double energy){
    this.name = name;
    this.position = new Position(position.getX(), position.getY());
    this.energy = energy;
  }

  public void Rename(String name){
    this.name = name;
  }

  /*
  // X方向Delta, Y方向yDelta移動(相対移動)
  public void move(int xDelta, int yDelta){
    this.position.move(xDelta, yDelta);
  }
  */

  // 2.からの修正 絶対移動

  // ロボットを座標位置(絶対移動)に移動して移動距離を返す
  public double move(int x, int y){

    System.out.println("moveします");

    double distancePurposeCurrent;    // 現在地と目標地点までの距離
    double distanceStCurrent;         // 現在地の最寄りにあるステーションまでの距離
    double deltaE;                 // 供給するエネルギー量
    double distanceStPurpose; // 目的地の最寄りにあるステーションまでの距離

    int xPosition = this.position.getX(); // 現在の位置を取得
    int yPosition = this.position.getY();

    //現在地と目標地点までの距離を計算
    distancePurposeCurrent = Math.sqrt(Math.pow((x - xPosition), 2)
                       + Math.pow((y - yPosition), 2));

    System.out.println("現在地と目的地までの距離 : " + distancePurposeCurrent);

    // 現在地の近くにあるステーションを探す
    Station st = Stations.get_station(xPosition, yPosition);
    Position p = st.get_Position();     // 現在地の最寄りのステーションの場所を受け取る
    int xStCurrent = p.getX();          // 最寄りの場所のx座標
    int yStCurrent = p.getY();          // 最寄りの場所のy座標

    System.out.println("現在地の座標x : " + xPosition);
    System.out.println("現在地の座標y : " + yPosition);
    System.out.println("現在地近くのSt座標 x : " + xStCurrent);
    System.out.println("現在地近くのSt座標 y : " + yStCurrent);

    distanceStCurrent = Math.sqrt(Math.pow((xPosition - xStCurrent), 2)
                                + Math.pow((yPosition - yStCurrent), 2));

    System.out.println("現在地から最寄りのステーションまでの距離 : " + distanceStCurrent);


    // 目的地の最寄りにあるステーションと目的地までの距離を求める
    Station stPurpose = Stations.get_station(x, y);
    Position pStPurpose = stPurpose.get_Position();
    double xStPurpose = pStPurpose.getX();
    double yStPurpose = pStPurpose.getY();
    distanceStPurpose = Math.sqrt(Math.pow((xStPurpose - x), 2)
                                + Math.pow((yStPurpose - y), 2));

    // 目的地までのエネルギーが足りなかった場合
    if((this.energy - distancePurposeCurrent - distanceStPurpose) < 0){

      System.out.println("燃料が少ないので、最寄りのステーションへ移動します.");
      // 最寄りのステーションへ絶対移動する
      this.position.move(-xPosition, -yPosition); // 一度原点へ移動
      this.position.move(xStCurrent, yStCurrent); //原点から最寄りのステーションへ

      System.out.println("現在のエネルギー(現在地地点) : " + this.energy);
      System.out.println("消費エネルギー(現在地の最寄りのステーション) : " + distanceStCurrent);

      this.energy -= distanceStCurrent; // 最寄りのステーションまでのエネルギー消費

      System.out.println("現在のエネルギー : " + this.energy);

      // 現在地近くのステーションと目的地までの距離を求める
      double tmpDistance = Math.sqrt(Math.pow((xStCurrent - x), 2)
                                   + Math.pow((yStCurrent - y), 2));

      // ΔE = ステーションから目的地までのE + 目的地から最寄りのステーションまでのE
      deltaE = tmpDistance + distanceStPurpose; // チャージするエネルギーΔEを計算
      System.out.println("ΔE : " + deltaE);

      st.set_robot(this); // 自分をステーションにセット
      st.supply(deltaE); // エネルギーをΔE分チャージ

      // 最寄りのステーションから目的地まで絶対移動
      this.position.move(-xStCurrent, -yStCurrent);  // 一度原点へ移動
      this.position.move(x, y); // 原点から目的地へ移動

      this.energy -= tmpDistance;  // 目的地までのエネルギー消費

      return distancePurposeCurrent;  // 現在地から目的地までの距離を返す
    }

    // 現在のエネルギーで目的地へ移動できる場合の絶対移動
    this.position.move(-xPosition, -yPosition); // 相対移動で原点へ
    this.position.move(x, y);       // 絶対移動する座標へ相対移動

    this.energy -= distancePurposeCurrent;

    return distancePurposeCurrent;

  }

  // ロボットの名前を得る
  public String getName(){
    return this.name;
  }

  // ロボットの現在の位置を得る
  public Position getPosition(){
    return this.position;
  }

  // (4)クローン生成
  public Robot make_clone(String name){
    // クローンのインスタンス生成
    position = new Position(this.position.getX(), this.position.getY());

    Robot rc = new Robot(position, name);
    return rc;
  }

  public double getDistance(Robot robot){
    double distance;              // 距離

    int x = this.position.getX(); // 自分のx
    int y = this.position.getY(); // 自分のy
    int x_c = robot.position.getX();       // cloneのx
    int y_c = robot.position.getY();       // cloneのy

    /*
    System.out.println(x);
    System.out.println(y);
    System.out.println(x_c);
    System.out.println(y_c);
    */

    distance = Math.sqrt(Math.pow((x - x_c), 2) + Math.pow((y - y_c), 2));

    return distance;
  }

  public double getDistance(int x_c, int y_c){
    double distance;              // 距離

    int x = this.position.getX(); // 自分のx
    int y = this.position.getY(); // 自分のy

    distance = Math.sqrt(Math.pow((x - x_c), 2) + Math.pow((y - y_c), 2));

    return distance;
  }

  // 2.からの追加メソッド

  public double getEnergy(){      // ロボットの現在のエネルギー量を返す
    return this.energy;
  }

  public void receiveEnergy(double quantity){ // エネルギーを受給
    this.energy += quantity;
  }

}
