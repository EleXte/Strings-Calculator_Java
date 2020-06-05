package calculator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.regex.*;

public class Calculator {

    /**
     * @param args the command line arguments
     */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable()
		{
			public void run() {
				CalculatorFrame frame = new CalculatorFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

class CalculatorFrame extends JFrame {
	public CalculatorFrame() {
		setTitle("Calculator");
		CalculatorPanel panel = new CalculatorPanel();
		add(panel);
		pack();
	}
}

class CalculatorPanel extends JPanel {
	public CalculatorPanel() {
		setLayout(new BorderLayout());
 
		start = true;
 
		display = new JButton("0");
		display.setEnabled(false);
		add(display, BorderLayout.NORTH);
 
		ActionListener insert = new InsertAction();
		ActionListener command = new CommandAction();
 
		panel = new JPanel();
		panel.setLayout(new GridLayout(5, 4));
 
		addButton(".", insert);
                addButton("C", command);
		addButton("<-", command);
                addButton("=", command);
                
		addButton("7", insert);
		addButton("8", insert);
		addButton("9", insert);
		addButton("/", insert);
 
		addButton("4", insert);
		addButton("5", insert);
		addButton("6", insert);
		addButton("*", insert);
 
		addButton("1", insert);
		addButton("2", insert);
		addButton("3", insert);
		addButton("-", insert);
 
		addButton("0", insert);
		addButton("(", insert);
		addButton(")", insert);
		addButton("+", insert);
 
		add(panel, BorderLayout.CENTER);
	}
        
	private void addButton(String label, ActionListener listener) {
		JButton button = new JButton(label);
		button.addActionListener(listener);
		panel.add(button);
	}
        
	private class InsertAction implements ActionListener 
	{
		public void actionPerformed(ActionEvent event) 
		{
                    String input = event.getActionCommand();
                    if(start == true) {
                    	display.setText("");
			start = false;
                    }
                    display.setText(display.getText() + input);
		}
	}
        
	private class CommandAction implements ActionListener 
	{
		public void actionPerformed(ActionEvent event) 
		{
			String command = event.getActionCommand();
			if(command.equals("C")) { //Команда обнуления
                            display.setText("0");
                            start = true;
			} else if (command.equals("<-")) { //Команда удаления последнего символа
                            String str = display.getText();
                            if (str.length() > 1 ) {
                                str = str.substring(0, str.length()-1);
                                display.setText("" + str);
                            } else if (str.length() == 1) {
                                display.setText("0");
                                start = true;
                            }
                        } else { //Команда подсчёта
                            notacing = display.getText();
                            parse();
                            start=true;
			}
		}	
	}
        //Подсчитываем пару значений
	public String calculate(String[] str, String sym)
	{
            double x;
            double y;
            String end;
            for (int i = 0; i < str.length; i++) { //Обратная замена временного знака минус
                if (str[i].contains("±") == true) {
                    str[i] = str[i].replaceAll("±", "-");
                }
            }
            x = Double.parseDouble(str[0]);
            y = Double.parseDouble(str[1]);
            if(sym.equals("+")) { //Считаем
                x += y;
            } else if(sym.equals("-")) {
                x -= y;
            } else if(sym.equals("*")) {
                x *= y;
            } else if(sym.equals("/")) {
                x /= y;
            }
            if (x < 0) { //Замена на временный знак минус
                end = Double.toString(x);
                return end = end.replaceAll("-", "±");
            } else {
                return Double.toString(x);
            }
	}
        //Генеральная функция обработки
        public void parse()
	{
            String str = notacing;
            str = parseMinus(str, ptrnDecr1); //Первый отрицательный разбор
            str = parseMinus(str, ptrnDecr2); //Второй отрицательный разбор
            str = parseZeroPrior(str); //Поиск и разбор выражения в скобках
            str = parsePrior(str, ptrnPt1); //Поиск и подсчёт первого приоритета
            str = parsePrior(str, ptrnPt2); //Поиск и подсчёт второго приоритета
            if (str.contains("±") == true) { //Отсюда и до конца процедура вывода результата
                str = str.replaceAll("±", "-");
            }
            double x = Double.parseDouble(str);
            if (Math.floor(x) == x) {
                String[] str2 = str.split("[.]");
                str = str2[0];
            }
            display.setText("" + str);
	}
        //Конвертация форм минуса в не мешающую форму
        public String parseMinus(String str, String paterns) {
            Pattern p;
            Matcher m;
            p = Pattern.compile(paterns);  
            m = p.matcher(str);  
            while(m.find() == true){  
                String newStr; 
                newStr = str.substring(m.start(), m.end());
                newStr = newStr.replaceAll("[(]|[)]", "");
                newStr = newStr.replaceAll("-", "±");
                str = str.replaceFirst(paterns, newStr);
                m = p.matcher(str);
            }
            return str;
        }
        //Обработка значение в скобках
        public String parseZeroPrior(String str)
	{
            Pattern p = Pattern.compile(ptrnPt0);  
            Matcher m = p.matcher(str);  
            while(m.find() == true ){  //Нашли
                String newStr;
                newStr = str.substring(m.start(), m.end());
                newStr = newStr.replaceAll("[(]|[)]", ""); //Убрали
                newStr = parsePrior(newStr, ptrnPt1); //Проверяем/вычисляем шаблон первого приоритета
                newStr = parsePrior(newStr, ptrnPt2); //Проверяем/вычисляем шаблон второго приоритета
                str = str.replaceFirst(ptrnPt0, newStr); //Возвращяем расчитанное значение
                m = p.matcher(str);
            }
            return str;
	}
        //Поиск пар и расчёт
        public String parsePrior(String str, String paterns)
	{
            Pattern p;
            Matcher m;
            p = Pattern.compile(paterns); //Подготавливаем поиск пар
            m = p.matcher(str);  
            while(m.find() == true ){  //Нашли
                String newStr;
                newStr = m.group();
                Pattern p2 = Pattern.compile(ptrnSymbol); //Настраиваем поиск знака
                Matcher sym = p2.matcher(newStr);
                sym.find();
                newStr = calculate(newStr.split(ptrnSymbol), sym.group()); //Отправляем пару со знаком на подсчёт
                str = str.replaceFirst(paterns, newStr); //Возвращяем значение
                m = p.matcher(str);
            }           
            return str;
	}            
            
	private JButton display;
	private JPanel panel;
        private String notacing; //Переменная введенной нотации
	private boolean start; //Переменная старта обработки для правильного ввода
        
        //Шаблоны для замены минуса формата "(-n)" и "-n"
        private static String ptrnDecr1 = "([(][-][0-9.]+[)])";
        private static String ptrnDecr2 = "([\\/\\*\\-\\+][\\-][0-9.]+)";
        //Шаблон замены скобок
        private static String ptrnPt0 = "([(][0-9\\/\\*\\-\\+\\±.]+[)])";
        //Шаблоны приоритетов операций. 1 для "*" и "/" 2 для "+" и "-"
        private static String ptrnPt1 = "([±]*)([0-9.]+)([\\/]|[\\*])([±]*)([0-9.]+)";
        private static String ptrnPt2 = "([±]*)([0-9.]+)([\\-]|[\\+])([±]*)([0-9.]+)";
        //Шаблон выбора символа операции
        private static String ptrnSymbol = "[\\/]|[\\*]|[\\-]|[\\+]";
}
