package hr.knezzz.randomsolarsystem.utils;

/**
 * Created by knezzz on 28/03/16.
 */
public interface Resources {
    // solar radius = 695,700km
    // solar mass = 1.989 × 10^30 kg
    boolean DEVELOPER_MODE = false;
    boolean GENERATE_TERRAIN = true;

    String PLANET_INTENT_WHOLE_PLANET = "PLANET";
    String PLANET_INTENT_RECEVER = "REC";

    String PLANET_NAME_LETTERS = "abcdefghijklmnopqrstuvwxyzaeiouyhdaeiou";

    int PLANET_ANIMATION_TIME = 16;//16ms for 60fps

    double SHORTEST_SUN_DISTANCE = 0.005;
    double BIGGEST_SUN_DISTANCE = 84000;

    long MODEL_BIGGEST_DISTANCE = 600;

    int SMALLEST_SMALL_PLANET = 4096; //10 000km diameter 2^12
    int BIGGEST_SMALL_PLANET = 65536;//393216; //400 000km diameter 2^17+2^18

    int SMALLEST_BIG_PLANET = BIGGEST_SMALL_PLANET;
    int BIGGEST_BIG_PLANET = 393216; //400 000km diameter 2^17+2^18

    double BIGGEST_SUN = 10.8;

    int STAR_TYPE_O = 1;
    /*
    color : blue
    temperature : ≥ 30,000 K
    weight : >16M (solar mass)
    size : >6.6 (solar radius)
    chances : 0.00003%
     */
    int STAR_TYPE_B = 2;
    /*
    color : blue white
    temperature : 10,000 - 30,000 K
    weight : 2.1 - 16M (solar mass)
    size : 1.8 - 6.6 (solar radius)
    chances : 0.13%
     */
    int STAR_TYPE_A = 3;
    /*
    color : white
    temperature : 7,500 - 10,000 K
    weight : 1.4 - 2.1 (solar mass)
    size : 1.4 - 1.8 (solar radius)
    chances : 0.6%
     */
    int STAR_TYPE_F = 4;
    /*
    color : yellow white
    temperature : 6,000 - 7,500 K
    weight : 1.04 - 1.4 (solar mass)
    size : 1.15 - 1.4 (solar radius)
    chances : 3%
    */
    int STAR_TYPE_G = 5;
    /*
    color : yellow
    temperature : 5,200 - 6,000 K
    weight : 0.8 - 1.04 (solar mass)
    size : 0.96 - 1.15 (solar radius)
    chances : 7.6%
    */
    int STAR_TYPE_K = 6;
    /*
    color : orange
    temperature : 3,700 - 5,200 K
    weight : 0.45 - 0.8 (solar mass)
    size : 0.7 - 0.96 (solar radius)
    chances : 12.1%
    */
    int STAR_TYPE_M = 7;
    /*
    color : red
    temperature : 2,400 - 3,700 K
    weight : 0.08 - 0.45 (solar mass)
    size : < 0.7 (solar radius)
    chances : 76.45%
    */


    int STAR_SUBTYPE_EXTREMELY_LUMINOUS_SUPERGIANT = 0;
    int STAR_SUBTYPE_LUMINOUS_SUPERGIANT = 1;
    int STAR_SUBTYPE_LESS_LUMINOUS_SUPERGIANT = 3;
    int STAR_SUBTYPE_BRIGHT_GIANT = 4;
    int STAR_SUBTYPE_GIANT = 5;
    int STAR_SUBTYPE_SUBGIANT = 6;
    int STAR_SUBTYPE_MAIN_SEQUANCE_STAR = 7;
    int STAR_SUBTYPE_SUBDWARFS = 8;
    int STAR_SUBTYPE_WHITE_DWARF = 9;
}
