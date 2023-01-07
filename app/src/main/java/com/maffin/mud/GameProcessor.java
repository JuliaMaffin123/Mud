package com.maffin.mud;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.widget.TextView;

public class GameProcessor {

    public static final String STOP = "стоп";
    public static final String LOOK = "смотреть";
    public static final String FAIL = "YOU DIED";

    private Context context;
    private MediaPlayer mp;

    private int hp = 100;                   // Здоровье
    private int st = 100;                   // Стамина
    private int booter = 3;                 // Бутеры
    private int room = 0;                   // Текущая комната
    private int prev = 0;                   // Предыдущая комната
    private int stone = 2;                  // Где сейчас камень
    private int log = -1;                   // Где сейчас бревно
    private String command = LOOK;          // Текущая команда
    private boolean endGame = false;        // Признак завершения игры
    private int meditation = 0;             // Число непрерывных медитаций

    public GameProcessor(Context context) {
        this.context = context;
    }


    public static void appendText(TextView tv, String text) {
        tv.append(text);
    }

    public static void appendColoredText(TextView tv, String text, boolean br) {
        String sourceString = "<b><span style=\"color:#F6200EE;\">" + text + "</span></b>" + (br ? "<br>" : "");
        tv.append(Html.fromHtml(sourceString));
    }

    public void startGame(TextView tv) {
        // Сброс значений
        hp = 100;                   // Здоровье
        st = 100;                   // Стамина
        booter = 3;                 // Бутеры
        room = 0;                   // Текущая комната
        prev = 0;                   // Предыдущая комната
        stone = 2;                  // Где сейчас камень
        log = -1;                   // Где сейчас бревно
        command = LOOK;             // Текущая команда
        endGame = false;            // Признак завершения игры

        // Отрисовка приветствия
        tv.setText("");
        appendText(tv, getGreetings());
        appendText(tv, getHelp());
        appendColoredText(tv, getState(), false);
        playSound(R.raw.forest, true);
    }

    public void endGame() {
        endGame = true;
        if (mp != null) {
            mp.stop();
        }
    }

    public boolean isEndGame() {
        return endGame;
    }

    public String getState() {
        return "[" + hp + "хп " + st + "ст]: ";
    }

    public String getGreetings() {
        StringBuilder sb = new StringBuilder();
        sb.append("---=== Добро пожаловать в MUD ===---").append("\r\n");
        sb.append("В нашей игре Вы можете совершать различные действия из списка команд. Попробуйте выбраться из ЗАЧАРОВАННОГО леса.").append("\r\n");
        sb.append("").append("\r\n");
        sb.append("Удачи !!! Она Вам понадобится :)").append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================").append("\r\n");
        sb.append("|             КОМАНДЫ              |").append("\r\n");
        sb.append("====================================").append("\r\n");
        sb.append("| смотреть, выходы, стоп, взять,   |").append("\r\n");
        sb.append("| инвентарь, использовать,         |").append("\r\n");
        sb.append("| север, юг, запад, восток,        |").append("\r\n");
        sb.append("| помощь, ?, сказать, кричать,     |").append("\r\n");
        sb.append("| медитация                        |").append("\r\n");
        sb.append("====================================").append("\r\n");
        return sb.toString();
    }

    public void process(TextView tv, String command) {
        // 1. Выводим команду
        appendColoredText(tv, command, true);
        // 2. Обрабатываем команду
        appendText(tv, next(command));
        // 3. Выводим ХП и СТ
        if(!isEndGame()) {
            appendColoredText(tv, getState(), false);
        }
    }

    private String next(String command) {
        StringBuilder sb = new StringBuilder();
        // Обработка команд
        if ("?".equals(command) || command.startsWith("по")) {
            return getHelp();
        } else if(STOP.equals(command)) {
            endGame();
            return "Спасибо за участие. Приходите еще раз с друзьями!\r\n";
        } else if(command.startsWith("ме")) {
            // МЕДИТАЦИЯ
            meditation += 1;
            if (meditation == 5) {
                sb.append("Поздравляем! Вы стали прислужником Н'Зота!").append("\r\n");
                sb.append("Вы находитесь в самом мрачном овраге на свете! А запах... Бррр! Вы опирается на сучковатую дубину, похожую на небольшой ствол дерева. А перед Вами стоит робкий путник с сумкой через плечо...").append("\r\n");
                sb.append("Al'ksh syq iir awan? Iilth sythn aqev… aqev… aqev…").append("\r\n");
                endGame = true;
                playSound(R.raw.win, false);
                return sb.toString();
            } else {
                sb.append("Вы уселись в позу лотоса и провели время в приятной медитаци. Отдохнули что надо, но здоровья это не добавило...").append("\r\n");
                st += 20;
                hp -= 10;
            }
        } else if(command.startsWith("ис")) {
            // ИСПОЛЬЗОВАТЬ
            meditation = 0;
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                return "Вы совершаете непонятные пассы руками.\r\n";
            } else {
                String obj = sub[1];
                if (obj.startsWith("ка") && stone == 0) {
                    if (room == 4) {
                        sb.append("Вы отпраляете камень точнёхонько огру в глаз!").append("\r\n");
                        sb.append("Огр взревел как раненый ОГР, перехватил дубину поудобнее и...").append("\r\n");
                        sb.append("Запульнул в Вас дубиной как в городках!").append("\r\n");
                        stone = -1;
                        log = 4;
                        room = 3;
                        hp -= 50;
                        if (hp > 0) {
                            sb.append("...").append("\r\n");
                            sb.append("Вас оглушило и отбросило назад.").append("\r\n");
                        }
                    } else {
                        sb.append("Вы роняете камень на землю.").append("\r\n");
                        stone = room;
                    }
                } else if (obj.startsWith("бр") && log == 0) {
                    sb.append("Вы приставляете бревно к склону и взбираетесь по нему...").append("\r\n");
                    if (room == 2) {
                        endGame = true;
                        sb.append("БИНГО! Вы выбрались из оврага и покинули ЗАЧАРОВАННЫЙ лес!").append("\r\n").append("\r\n");
                        sb.append("Спасибо за участие. Приходите еще раз с друзьями!").append("\r\n");
                        playSound(R.raw.win, false);
                        return sb.toString();
                    } else {
                        sb.append("К сожалению Вам не хватило совсем чуть чуть.").append("\r\n");
                        sb.append("Вы теряете равновесие и падаете на дно оврага.").append("\r\n");
                        sb.append("Бревно больно бьет Вас по голове!").append("\r\n");
                        hp -= 20;
                        log = room;
                    }
                } else if ("огр".equals(obj) && room == 4) {
                    sb.append("Вы это серьёзно? Он же Вас сожрёт!").append("\r\n");
                } else if (obj.startsWith("су") || obj.startsWith("бу")) {
                    if (booter == 0) {
                        sb.append("К сожалению все бутеры закончились.").append("\r\n");
                    } else {
                        booter -= 1;
                        if (room == 4) {
                            sb.append("Вы бросаете бутер огру.").append("\r\n");
                            sb.append("Огр страшно щелкает челюстью и налету сглатывает бутер.").append("\r\n");
                            sb.append("Кажется у него сильнее стали течь слюни!").append("\r\n");
                        } else {
                            sb.append("Вы с большим удовольствием съедаете бутер.").append("\r\n");
                            hp += 20;
                        }
                    }
                } else {
                    sb.append("Вы не видите здесь такого.").append("\r\n");
                }
            }
        } else if (command.startsWith("вз")) {
            // ВЗЯТЬ
            meditation = 0;
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                return "Вы бесмысленно хватаете руками воздух.\r\n";
            } else {
                String obj = sub[1];
                if (obj.startsWith("ка") && room == stone) {
                    stone = 0;
                    sb.append("Вы поднимаете камень с земли.").append("\r\n");
                } else if (obj.startsWith("бр") && room == log) {
                    log = 0;
                    sb.append("Вы взваливаете бревно на плечо.").append("\r\n");
                } else if ("огр".equals(obj) && room == 4) {
                    sb.append("Вы это серьёзно? Он же Вас сожрёт!").append("\r\n");
                } else {
                    sb.append("Вы не видите здесь такого.").append("\r\n");
                }
            }
        } else if (command.startsWith("ск")) {
            // СКАЗАТЬ
            meditation = 0;
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                return "Вы что-то бормочите себе под нос...\r\n";
            } else {
                String obj = command.substring(sub[0].length());
                sb.append("Вы говорите: ").append(obj).append("\r\n");
                if (room == 3) {
                    sb.append("Огр начинает подозрительно принюхиваться!").append("\r\n");
                } else {
                    sb.append("Вас никто не слышит...").append("\r\n");
                }
            }
        } else if (command.startsWith("кр")) {
            // КРИЧАТЬ
            meditation = 0;
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                return "Вы издаёте пронзительный крик! Тарзан бы позавидовал Вам!\r\n";
            } else {
                String obj = command.substring(sub[0].length());
                sb.append("Вы кричите: ").append(obj).append("\r\n");
                if (room == 4) {
                    sb.append("Огр вздрагивает и рычит Вам в лицо:").append("\r\n");
                    sb.append("\t").append("Uovssh thyzz… qwaz…!").append("\r\n");
                    sb.append("\t").append("Mg'uulwi N'Zoth, eth'razzqi worg zz oou.").append("\r\n");
                    sb.append("\t").append("Gul'kafh an'qov N'Zoth.").append("\r\n");
                } else {
                    sb.append("Вас никто не слышит...").append("\r\n");
                }
             }
        } else if (command.startsWith("см")) {
            // СМОТРЕТЬ
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                // Выводим описание комнаты
                if (room == 0) {
                    sb.append("Сегодня отличная погода для прогулки по лесу.").append("\r\n");
                    sb.append("Тропинка привела Вас на опушку, залитую солнцем.").append("\r\n");
                    sb.append("Заросли плотно обступили опушку, но на востоке виднеется просвет.").append("\r\n");
                } else if (room == 1) {
                    if (prev == 0) {
                        sb.append("Небесный горошек! Какой же Вы неосторожный!").append("\r\n");
                        sb.append("Нога подскользнулась на какой-то непонятной субстанции и вы съехали в овраг!").append("\r\n");
                    }
                    sb.append("Здесь сыро, темно и отчетливо попахивает опасностью!").append("\r\n");
                    sb.append("Склоны оврага выглядят неприступными.").append("\r\n");
                } else if (room == 2) {
                    sb.append("Кажется в этом месте склоны оврага стали пониже.").append("\r\n");
                    sb.append("Вверху виднеется просвет в листве и слышно щебетание птиц.").append("\r\n");
                } else if (room == 3) {
                    sb.append("Вы находитесь в самом мрачном овраге на свете!").append("\r\n");
                    sb.append("На западе он посуше и посветлее.").append("\r\n");
                    sb.append("А вот на восток лучше бы не ходить!").append("\r\n");
                    sb.append("Там отчетливо слышна чья-то грузная возня и ворчание. А запах... Бррр!").append("\r\n");
                } else if (room == 4) {
                    sb.append("А я вас предупреждал! Не надо сюда ходить!").append("\r\n");
                    sb.append("Жуткого вида огр загораживает выход из оврага и собирается Вами закусить!").append("\r\n");
                    sb.append("Огр опирается на сучковатую дубину, похожую на небольшой ствол дерева.").append("\r\n");
                }
                // Дополнитеьные объекты
                if (room == stone) {
                    sb.append(" * Под ногами лежит небольшой камень.").append("\r\n");
                }
                if (room == log) {
                    sb.append(" * Приличных размеров бревно валяется тут.").append("\r\n");
                }
            } else {
                // Смотреть на что-то
                String obj = sub[1];
                if (obj.startsWith("су")) {
                    sb.append("Удобная холщевая сумка. Такие обычно носят через плечо.").append("\r\n");
                    if (booter == 0) {
                        sb.append("Сумка пуста, на дне болтается пара крошек от бутеров.").append("\r\n");
                    } else {
                        sb.append("В сумке лежит коробочка, а в ней ").append(booter).append(" бутера от бабули.").append("\r\n");
                    }
                } else if (obj.startsWith("ка")) {
                    if (room == stone || stone == 0) {
                        sb.append("Камень небольшой, но на вид удобный, чтобы запустить кому-то в лоб!").append("\r\n");
                    } else {
                        sb.append("Вы не видите здесь камней.").append("\r\n");
                    }
                } else if (obj.startsWith("бр")) {
                    if (room == log || log == 0) {
                        sb.append("Сучковатое бревно, довольно крепкое, чтобы выдержать Ваш вес.").append("\r\n");
                    } else {
                        sb.append("Вы не видите здесь бревен.").append("\r\n");
                    }
                } else if (obj.startsWith("ог")) {
                    if (room == 4) {
                        sb.append("Перед Вами самый отвратительный представитель этой рассы.").append("\r\n");
                        sb.append("Единственный глаз заплыл синяком. Кто-то недавно бросил в него камнем?").append("\r\n");
                        sb.append("Из пасти капает слюна. Огр явно голоден и зол!").append("\r\n");
                    } else {
                        sb.append("Судя по запаху огр где-то рядом, но Вы его не видите.").append("\r\n");
                    }
                } else if (obj.startsWith("ю")) {
                    if (room == 0) {
                        sb.append("Вас окружают заросли и не понятно с какой стороны Вы пришли!").append("\r\n");
                        sb.append("Попробуйте поискать выходы.").append("\r\n");
                    }
                    if (room == 1) {
                        sb.append("На юге виднеется узкий проход. Кажется там стенки оврага пониже.").append("\r\n");
                    }
                    if (room == 2) {
                        sb.append("Вы в тупиковом ответвлении оврага.").append("\r\n");
                        sb.append("Стенки здесь не такие высокие, но без помощи все равно не выбраться.").append("\r\n");
                    }
                    if (room == 3) {
                        sb.append("Очень высокая стена, испещренная подозрительными ходами.").append("\r\n");
                    }
                    if (room == 4) {
                        sb.append("Там нет ничего интересного.").append("\r\n");
                        sb.append("Вы бы лучше думали о страшном огре, который перегородил единственный выход из оврага!").append("\r\n");
                    }
                } else if (obj.startsWith("с")) {
                    if (room == 0) {
                        sb.append("Вас окружают заросли и не понятно с какой стороны Вы пришли!").append("\r\n");
                        sb.append("Попробуйте поискать выходы.").append("\r\n");
                    }
                    if (room == 1) {
                        sb.append("Довольно крутая стена. До края не допрыгнуть...").append("\r\n");
                    }
                    if (room == 2) {
                        sb.append("В той стороне овраг понижается и становится темнее.").append("\r\n");
                    }
                    if (room == 3) {
                        sb.append("Очень высокая стена из которой сочится влага.").append("\r\n");
                    }
                    if (room == 4) {
                        sb.append("Там нет ничего интересного.").append("\r\n");
                        sb.append("Вы бы учше думали о страшном огре, который перегородил единственный выход из оврага!").append("\r\n");
                    }
                } else if (obj.startsWith("з")) {
                    if (room == 0) {
                        sb.append("Вас окружают заросли и не понятно с какой стороны Вы пришли!").append("\r\n");
                        sb.append("Попробуйте поискать выходы.").append("\r\n");
                    }
                    if (room == 1) {
                        sb.append("Довольно крутая стена. До края не допрыгнуть...").append("\r\n");
                    }
                    if (room == 2) {
                        sb.append("Вы в тупиковом ответвлении оврага.").append("\r\n");
                        sb.append("Стенки здесь не такие высокие, но без помощи все равно не выбраться.").append("\r\n");
                    }
                    if (room == 3) {
                        sb.append("В том направлении овраг повышается. Там Вы попали в ловушку.").append("\r\n");
                    }
                    if (room == 4) {
                        sb.append("Хороший вариант избежать разборок с огром.").append("\r\n");
                        sb.append("Небольшой ручеек сбегает по дну оврага с той стороны.").append("\r\n");
                    }
                } else if (obj.startsWith("в")) {
                    if (room == 0) {
                        sb.append("В зарослях виднеется небольшой просвет и начало тропинки.").append("\r\n");
                        sb.append("Будьте острожны и смотрите под ноги, тропинка выглядит сырой.").append("\r\n");
                    }
                    if (room == 1) {
                        sb.append("Дно оврага идет под уклон. С той стороны тянет сыростью и чем-то пованивает.").append("\r\n");
                    }
                    if (room == 2) {
                        sb.append("Вы в тупиковом ответвлении оврага.").append("\r\n");
                        sb.append("Стенки здесь не такие высокие, но без помощи все равно не выбраться.").append("\r\n");
                    }
                    if (room == 3) {
                        sb.append("Дно оврага идет под уклон. С той стороны тянет сыростью и чем-то пованивает.").append("\r\n");
                    }
                    if (room == 4) {
                        sb.append("За спиной огра виден выход из оврага.").append("\r\n");
                        sb.append("Вот только как же обойти эту агрессивную гору мяса?..").append("\r\n");
                    }
                } else {
                    sb.append("Вы задумчиво смотрите на [").append(obj).append("]...").append("\r\n");
                    sb.append("Может пора поискать выходы?").append("\r\n");
                }
            }
            return sb.toString();
        } else if (command.startsWith("вы")) {
            // ВЫХОДЫ
            sb.append("Выходы: ").append("\r\n");
            if (room == 0) {
                sb.append("восток").append("\r\n");
            } else if (room == 1) {
                sb.append("юг восток").append("\r\n");
            } else if (room == 2) {
                sb.append("север").append("\r\n");
            } else if (room == 3) {
                sb.append("запад восток").append("\r\n");
            } else if (room == 4) {
                sb.append("запад восток").append("\r\n");
            }
        } else if (command.startsWith("ин")) {
            // ИНВЕНТАРЬ
            sb.append("У вас имеется:").append("\r\n");
            sb.append(" * маленькая сумка через плечо").append("\r\n");
            if (stone == 0) {
                sb.append(" * в руках камень, модель \"булыжник\"").append("\r\n");
            }
            if (log == 0) {
                sb.append(" * на плечо взвалено тяжеленное бревно").append("\r\n");
            }
        } else if (command.startsWith("с")) {
            // СЕВЕР
            meditation = 0;
            prev = room;
            if (room == 0) {
                st -= 2;
                return "Густые заросли не пускают Вас туда.\r\n";
            } else if (room == 1) {
                sb.append("Вы карабкаетесь по склону, цепляясь за корни.").append("\r\n");
                sb.append("На пол пути силы покидают Вас и Вы падаете на дно оврага!").append("\r\n");
                st -= 10;
                hp -= 5;
                drop();
            } else if (room == 2) {
                room = 1;
                if (log == 0) {
                    st -= 5;
                } else {
                    st -= 1;
                }
                return next(LOOK);
            } else if (room == 3) {
                sb.append("Вы карабкаетесь по скользкому склону.").append("\r\n");
                sb.append("При каждой попытке руки соскальзывают. Пустая трата сил!").append("\r\n");
                st -= 10;
                drop();
            } else if (room == 4) {
                sb.append("Огр выбрасывает правую лапу, безуспешно пытаясь схватить Вас.").append("\r\n");
                sb.append("Это было опасно!").append("\r\n");
            }
        } else if (command.startsWith("ю")) {
            // ЮГ
            meditation = 0;
            prev = room;
            if (room == 0) {
                st -= 2;
                return "Густые заросли не пускают Вас туда.\r\n";
            } else if (room == 1) {
                room = 2;
                if (log == 0) {
                    st -= 10;
                } else {
                    st -= 2;
                }
                return next(LOOK);
            } else if (room == 2) {
                sb.append("Вы карабкаетесь по склону, цепляясь за корни.").append("\r\n");
                sb.append("В последний момент силы покидают Вас и Вы скатываетесь вниз!").append("\r\n");
                st -= 10;
                drop();
            } else if (room == 3) {
                sb.append("Вы карабкаетесь по склону, используя норы как опору.").append("\r\n");
                sb.append("Кто-то больно кусает Вас за палец и Вы, потеряв равновесие, падаете вниз!").append("\r\n");
                st -= 10;
                hp -= 10;
                drop();
            } else if (room == 4) {
                sb.append("Огр выбрасывает левую лапу, безуспешно пытаясь схватить Вас.").append("\r\n");
                sb.append("Это было опасно!").append("\r\n");
            }
        } else if (command.startsWith("з")) {
            // ЗАПАД
            meditation = 0;
            prev = room;
            if (room == 0) {
                st -= 2;
                return "Густые заросли не пускают Вас туда.\r\n";
            } else if (room == 1) {
                sb.append("Вы карабкаетесь по склону, цепляясь за корни.").append("\r\n");
                sb.append("На пол пути силы покидают Вас и Вы падаете на дно оврага!").append("\r\n");
                st -= 10;
                hp -= 5;
                drop();
            } else if (room == 2) {
                sb.append("Вы карабкаетесь по склону, цепляясь за корни.").append("\r\n");
                sb.append("В последний момент силы покидают Вас и Вы скатываетесь вниз!").append("\r\n");
                st -= 10;
                drop();
            } else if (room == 3) {
                room = 1;
                if (log == 0) {
                    st -= 10;
                } else {
                    st -= 2;
                }
                return next(LOOK);
            } else if (room == 4) {
                room = 3;
                if (log == 0) {
                    st -= 10;
                } else {
                    st -= 2;
                }
                return next(LOOK);
            }
        } else if (command.startsWith("в")) {
            // ВОСТОК
            meditation = 0;
            prev = room;
            if (room == 0) {
                room = 1;
                st -= 1;
                hp -= 10;
                playSound(R.raw.nzoth, true);
                return next(LOOK);
            } else if (room == 1) {
                room = 3;
                if (log == 0) {
                    st -= 5;
                } else {
                    st -= 1;
                }
                return next(LOOK);
            } else if (room == 2) {
                sb.append("Вы карабкаетесь по склону, цепляясь за корни.").append("\r\n");
                sb.append("В последний момент силы покидают Вас и Вы скатываетесь вниз!").append("\r\n");
                st -= 10;
                drop();
            } else if (room == 3) {
                room = 4;
                if (log == 0) {
                    st -= 5;
                } else {
                    st -= 1;
                }
                return next(LOOK);
            } else if (room == 4) {
                sb.append("Огр хватает Вас и ...").append("\r\n");
                sb.append("...").append("\r\n");
                sb.append("...").append("\r\n");
                endGame = true;
                sb.append(FAIL).append("\r\n");
                playSound(R.raw.died, false);
            }
        } else {
            sb.append("Я вас не понимаю. Попробуйте получить справку: ? или помощь.").append("\r\n");
        }
        // ПРОВЕРИМ ХП и СТАМИНУ
        if (hp <= 0 || st <= 0) {
            if (hp <= 0) {
                sb.append("Жизнь медленно вытекает через полученные раны...").append("\r\n");
            } else {
                sb.append("Вы так устали от бесконечных попыток выбраться.").append("\r\n");
                sb.append("Сил больше не осталось...").append("\r\n");
            }
            sb.append("...").append("\r\n");
            sb.append("...").append("\r\n");
            endGame = true;
            sb.append(FAIL).append("\r\n");
            playSound(R.raw.died, false);
        }
        return sb.toString();
    }

    private void drop() {
        // Бросить все
        if (log == 0) {
            log = room;
        }
        if (stone == 0) {
            stone = room;
        }
    }

    private void playSound(int music, boolean repeat) {
        new Thread() {
            public void run() {
                if (mp != null) {
                    mp.stop();
                }
                mp = MediaPlayer.create(context, music);
                mp.setLooping(repeat);
                mp.start();
            }
        }.start();
    }
}
