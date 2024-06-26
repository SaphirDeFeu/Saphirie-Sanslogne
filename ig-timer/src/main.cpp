#include<iostream>
#include<string>
#include<cstdint>

const std::string days_name[7] = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

typedef struct {
  int64_t year;
  uint16_t month;
  uint16_t day;
  std::string name;
} DateObject;

DateObject days_to_date(uint16_t days) {
  int64_t year = 1;
  uint16_t month = 1;
  uint16_t date = 1;

  uint16_t day_name_index = 0;

  uint16_t daysInMonth[12] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  while(days > 0) {
    uint16_t remainingDaysInMonth = daysInMonth[month - 1] - date + 1;


    if(days > remainingDaysInMonth) {
      days -= remainingDaysInMonth;
      day_name_index += remainingDaysInMonth;
      day_name_index %= 7;
      date = 1;
      month++;

      if(month > 12) {
        month = 1;
        year++;
      }
    } else {
      date += days - 1;
      day_name_index += days - 1;
      day_name_index %= 7;
      days = 0;
    }

  }

  DateObject obj;
  obj.year = year;
  obj.month = month;
  obj.day = date;
  obj.name = days_name[day_name_index];

  return obj;
};

std::string month_number_to_string(uint16_t number) {
  std::string months[12] = {
    "Janvier",
    "Fevrier",
    "Mars",
    "Avril",
    "Mai",
    "Juin",
    "Juillet",
    "Aout",
    "Septembre",
    "Octobre",
    "Novembre",
    "Décembre",
  };

  return months[number - 1];
};

int32_t main(int32_t argc, char** argv) {
  if(argc != 2) {
    std::cout << "./ig.exe <jours>" << std::endl << "Pour trouver <jour>, faites la commande `/time query day` et ajoutez 1 au resultat." << std::endl;
    return 0;
  }
  char* day_as_string = argv[1];

  uint16_t days = static_cast<uint16_t>(strtoul(day_as_string, NULL, 0));

  DateObject date = days_to_date(days);

  std::cout << date.name << " ";

  if(date.day == 1)
    std::cout << "1er ";
  else
    std::cout << date.day << " ";

  std::cout << month_number_to_string(date.month) << " " << date.year << std::endl;

  return 0;
};