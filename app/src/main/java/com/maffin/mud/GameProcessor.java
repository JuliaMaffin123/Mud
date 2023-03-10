package com.maffin.mud;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.widget.TextView;

/**
 * Движок игры.
 * Хранит текущее положение игрока, его характеристики и инвентарь.
 */
public class GameProcessor {

    public static final String STOP = "стоп";
    public static final String LOOK = "смотреть";
    public static final String FAIL = "YOU DIED";

    private Context context;
    private MediaPlayer mp;

    private int music = 0;                  // Текущая музыка

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
    private boolean mute = true;            // Режим без звука

    public GameProcessor(Context context) {
        this.context = context;
    }

    /**
     * Добавляет игровой текст основным цветом.
     * @param tv    TextView, в который будет выводиться текст игровых сообщений
     * @param text  текст
     */
    private void appendText(TextView tv, String text) {
        tv.append(text);
    }

    /**
     * Добавляет текст действия и характеристики игрока особым цветом.
     * @param tv    TextView, в который будет выводиться текст игровых сообщений
     * @param text  текст
     * @param br    нужен ли перенос строки
     */
    private void appendColoredText(TextView tv, String text, boolean br) {
        String sourceString = "<b><span style=\"color:#F018786;\">" + text + "</span></b>" + (br ? "<br>" : "");
        tv.append(Html.fromHtml(sourceString));
    }

    /**
     * Запуск игры.
     * @param tv    TextView, в который будет выводиться текст игровых сообщений
     */
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
        meditation = 0;             // Число непрерывных медитаций

        // Отрисовка приветствия
        tv.setText("");
        appendText(tv, getGreetings());
        appendText(tv, getHelp(false));
        appendColoredText(tv, getState(), false);
        playSound(R.raw.forest, true);
    }

    /**
     * Завершение игры.
     */
    public void endGame() {
        endGame = true;
        // Останавливаем воспроизведение звуков
        if (mp != null) {
            mp.stop();
        }
    }

    /**
     * Возвращает, завершена ли игра.
     * @return  true/false
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * Возвращает строку с описанием характеристик игрока.
     * @return  строка
     */
    private String getState() {
        return "[" + hp + "хп " + st + "ст]: ";
    }

    /**
     * Возвращает блок приветствия для начала игры.
     * @return  строка
     */
    private String getGreetings() {
        StringBuilder sb = new StringBuilder();
        sb.append("---=== Добро пожаловать в MUD ===---").append("\r\n");
        sb.append("В нашей игре Вы можете совершать различные действия из списка команд. Попробуйте выбраться из ЗАЧАРОВАННОГО леса.").append("\r\n");
        sb.append("").append("\r\n");
        sb.append("Удачи !!! Она Вам понадобится :)").append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    /**
     * Возвращает подсказку.
     * @param full  признак, нужна полная или сокращенная подсказка
     * @return  строка
     */
    public String getHelp(boolean full) {
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
        if (full) {
            sb.append("\r\n");
            sb.append("Некоторые команды могут состоять из двух слов. Например:").append("\r\n");
            sb.append("   ? {команда}").append("\r\n");
            sb.append("   использовать {предмет}").append("\r\n");
            sb.append("   смотреть {направление}").append("\r\n");
            sb.append("У вас есть небольшой запас здоровья (ХП) и выносливости (СТ). Следите, чтобы они не закончились, иначе ...").append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Обработка очередной команды игрока.
     * @param tv        TextView, в который будет выводиться текст игровых сообщений
     * @param command   комманда
     */
    public void process(TextView tv, String command) {
        // 1. Выводим команду
        appendColoredText(tv, command, true);
        // 2. Обрабатываем команду
        appendText(tv, next(command));
        // 3. Выводим ХП и СТ
        if (!isEndGame()) {
            appendColoredText(tv, getState(), false);
        }
    }

    /**
     * Формирует игровой ответ на комманду.
     * @param command   комманда
     * @return  строка
     */
    private String next(String command) {
        StringBuilder sb = new StringBuilder();
        // Обработка команд
        if (command.startsWith("?") || command.startsWith("по")) {
            String[] sub = command.split(" ");
            if (sub.length == 1) {
                return getHelp(true);
            } else {
                String obj = sub[1];
                if (obj.startsWith("см")) {
                    // см - смотреть
                    sb.append("Команда: смотреть").append("\r\n");
                    sb.append("Вы можете смотреть по сторонам или на что-то конкретное. Например:").append("\r\n");
                    sb.append("   см -> осмотреться вокруг").append("\r\n");
                    sb.append("   см ю -> посмотреть на ЮГ").append("\r\n");
                    sb.append("   см су -> осмотреть сумку").append("\r\n");
                } else if (obj.startsWith("вы")) {
                    // вы - выходы
                    sb.append("Команда: выходы").append("\r\n");
                    sb.append("Подсказывает в каких направлениях можно двигаться из текущей локации.").append("\r\n");
                } else if (obj.startsWith("ст")) {
                    // ст - стоп
                    sb.append("Команда: стоп").append("\r\n");
                    sb.append("Останавливает игру. Это единственная команда, которую надо набирать полностью. Нельзя же так легко сдаваться!").append("\r\n");
                } else if (obj.startsWith("вз")) {
                    // вз - взять
                    sb.append("Команда: взять").append("\r\n");
                    sb.append("Позволяет поднять с земли предметы (помните! некоторые из них могут быть тяжелыми). Например:").append("\r\n");
                    sb.append("   взять ка -> поднять камень").append("\r\n");
                } else if (obj.startsWith("ин")) {
                    // ин - инвентарь
                    sb.append("Команда: инвентарь").append("\r\n");
                    sb.append("Показывает, что Вы несете в руках.").append("\r\n");
                } else if (obj.startsWith("ис")) {
                    // ис - использовать
                    sb.append("Команда: использовать").append("\r\n");
                    sb.append("Предметы, которые Вы носите в инвентаре можно использовать. Например:").append("\r\n");
                    sb.append("   ис ка -> бросить камень").append("\r\n");
                } else if (obj.startsWith("ск")) {
                    // ск - сказать
                    sb.append("Команда: сказать").append("\r\n");
                    sb.append("Да, в игре можно говорить :) Например:").append("\r\n");
                    sb.append("   ска {что-то} -> произнести {что-то}").append("\r\n");
                } else if (obj.startsWith("кр")) {
                    // кр - кричать
                    sb.append("Команда: кричать").append("\r\n");
                    sb.append("В игре можно даже покричать. Попробуйте позвать на помощь :) Например:").append("\r\n");
                    sb.append("   кри {что-то} -> прокричать {что-то}").append("\r\n");
                } else if (obj.startsWith("ме")) {
                    // ме - медитация
                    sb.append("Команда: медитация").append("\r\n");
                    sb.append("Эта команда немного восстанавливает стамину за счет ХП. Будьте аккуратны!").append("\r\n");
                } else if (obj.startsWith("с")) {
                    // с - север
                    sb.append("Команда: север").append("\r\n");
                    sb.append("Двигаться на СЕВЕР, если это возможно.").append("\r\n");
                } else if (obj.startsWith("ю")) {
                    // ю - юг
                    sb.append("Команда: юг").append("\r\n");
                    sb.append("Двигаться на ЮГ, если это возможно.").append("\r\n");
                } else if (obj.startsWith("з")) {
                    // з - запад
                    sb.append("Команда: запад").append("\r\n");
                    sb.append("Двигаться на ЗАПАД, если это возможно.").append("\r\n");
                } else if (obj.startsWith("в")) {
                    // в - восток
                    sb.append("Команда: восток").append("\r\n");
                    sb.append("Двигаться на ВОСТОК, если это возможно.").append("\r\n");
                } else if (obj.startsWith("по")) {
                    // по - помощь
                    sb.append("Команда: помощь").append("\r\n");
                    sb.append("Выводит подсказки по командам. Например:").append("\r\n");
                    sb.append("   по -> общая справка").append("\r\n");
                    sb.append("   по ис -> справка по команде ИСПОЛЬЗОВАТЬ").append("\r\n");
                } else if (obj.equals("?")) {
                    // ?
                    sb.append("Команда: ?").append("\r\n");
                    sb.append("Выводит подсказки по командам. Например:").append("\r\n");
                    sb.append("   ? -> общая справка").append("\r\n");
                    sb.append("   ? ис -> справка по команде ИСПОЛЬЗОВАТЬ").append("\r\n");
                } else {
                    sb.append("Команда: медитация").append("\r\n");
                    sb.append("А что это за команда? Воспользуйтесь командой ? без параметров: чтобы посмотреть весь список.").append("\r\n");
                }
            }
        } else if(STOP.equals(command)) {
            endGame();
            return "Спасибо за участие. Приходите еще раз с друзьями!\r\n";
        } else if(command.startsWith("ме")) {
            // МЕДИТАЦИЯ
            meditation += 1;
            if (meditation == 5) {
                sb.append("Вы находитесь в самом мрачном овраге на свете! А запах... Бррр! Вы опираетесь на сучковатую дубину, похожую на небольшой ствол дерева, а перед Вами стоит робкий путник с сумкой через плечо...").append("\r\n");
                sb.append("Al'ksh syq iir awan? Iilth sythn aqev… aqev… aqev…").append("\r\n");
                sb.append("Поздравляем! Получено скрытое достижение \"Служитель Н'Зота!\"").append("\r\n");
                endGame = true;
                playSound(R.raw.win, false);
                return sb.toString();
            } else {
                sb.append("Вы взываете к Древним богам! Кто-то шепчет: Iilth ma paf'qi'ag sk'halahs... Вы чувствуете, как Ваше тело наполняется силой, но за нее нужно платить...").append("\r\n");
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
                return "Вы что-то бормочете себе под нос...\r\n";
            } else {
                String obj = command.substring(sub[0].length());
                sb.append("Вы говорите: ").append(obj).append("\r\n");
                if (room == 4) {
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
                        sb.append("Нога поскользнулась на какой-то непонятной субстанции и вы съехали в овраг!").append("\r\n");
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

    /**
     * Изменяет характеристики вещей (камень, бревно) при выпадении из инвентаря.
     */
    private void drop() {
        // Бросить все
        if (log == 0) {
            log = room;
        }
        if (stone == 0) {
            stone = room;
        }
    }

    /**
     * Запускает воспроизведение музыки.
     * @param musicId   ID ресурса
     * @param repeat    признак, надо ли повторять бесконечно
     */
    private void playSound(int musicId, boolean repeat) {
        new Thread() {
            public void run() {
                // Если до этого плеер был инициирован, стопнем музыку
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                }
                // Заряжаем в плеер новую мелодию
                setMusic(musicId);
                mp = MediaPlayer.create(context, music);
                // Ставим мелодию на повтор
                mp.setLooping(repeat);
                // Если режим не беззвучный - проигрываем сразу
                if (!mute) {
                    mp.start();
                }
            }
        }.start();
    }

    /**
     * Вкл/выкл возпроизведение звуков в приложении.
     * @param mute  признак, вкл/выкл звук
     */
    public void mute(boolean mute) {
        this.mute = mute;
        new Thread() {
            public void run() {
                if (mp != null) {
                    if (mute) {
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                    } else {
                        if (music != 0) {
                            mp = MediaPlayer.create(context, music);
                            mp.start();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Запоминает текущий, воспроизводимый звук.
     * @param musicId
     */
    public void setMusic(int musicId) {
        if (musicId != 0) {
            music = musicId;
        }
    }

    /**
     * Возвращает текущий, воспроизводимый звук.
     * @return
     */
    public int getMusic() {
        return music;
    }

}
