#include<iostream>
#include<vector>
#include<cmath>
#include<string>
#include<chrono>

using std::string;

void displayHelp() {
  std::cout << "MCTC par SaphirDeFeu" << std::endl << std::endl;
  std::cout << "  Utilisation: mctc.exe [option] [...args]" << std::endl << std::endl;
  std::cout << "Options:" << std::endl;
  std::cout << "  -h                        : Montrer ce message d'aide" << std::endl;
  std::cout << "  -t <entier dans 0..24000> : Calculer de ticks en temps reel" << std::endl;
  std::cout << "  -T <hh> <mm> <ss>         : Calculer de temps reel en ticks (hh est compris dans 0..24, mm, et ss sont compris dans 0..60)" << std::endl << std::endl;
}

string convertTicksToRT(const string& input) {
  // Convert string to number
  unsigned short ticks = static_cast<unsigned short>(std::strtoul(input.c_str(), NULL, 10));

  // Do some calculations idk
  unsigned short tick_hour = static_cast<unsigned short>(static_cast<float>(ticks) / 1000.0);
  unsigned short hour = (6 + tick_hour) % 24;

  unsigned short tick_minute = ticks % 1000;
  float minutes_with_sec = static_cast<float>(tick_minute) / (16.0 + 2.0 / 3.0);

  float sec = std::fmod(minutes_with_sec, 1.0) * 60.0;

  // Output result
  return std::to_string(hour) + ":" + std::to_string(static_cast<unsigned short>(minutes_with_sec)) + ":" + std::to_string(static_cast<unsigned short>(sec));
}

string convertRTToTicks(const string& hoursIn, const string& minsIn, const string& secsIn) {
  // Convert string to number
  unsigned short hour = static_cast<unsigned short>(std::strtoul(hoursIn.c_str(), NULL, 10));
  unsigned short minutes = static_cast<unsigned short>(std::strtoul(minsIn.c_str(), NULL, 10));
  unsigned short seconds = static_cast<unsigned short>(std::strtoul(secsIn.c_str(), NULL, 10));

  // Do calculations
  unsigned short results = (hour * 1000 + 18000) % 24000;

  unsigned short minutes_with_sec = static_cast<unsigned short>((static_cast<double>(seconds) / 60.0) * (16.0 + 2.0 / 3.0));
  minutes_with_sec += static_cast<unsigned short>(static_cast<double>(minutes) * (1000.0 / 60.0));

  results += minutes_with_sec;

  return std::to_string(results);
}

int interactiveMode() {
  std::cout << "Utilisation de MCTC Interactif..." << std::endl;
  std::cout << "  (1) : Calculer de ticks en temps reel" << std::endl;
  std::cout << "  (2) : Calculer de temps reel en ticks" << std::endl;
  std::cout << "  (n'importe) : Quitter" << std::endl << std::endl;

  int choice = 3;
  std::cout << "> ";
  std::cin >> choice;
  std::cout << std::endl;

  if(choice != 1 && choice != 2) {
    return 0;
  }

  if(choice == 1) {
    string s;
    std::cout << "Ticks > ";
    std::cin >> s;
    string res = convertTicksToRT(s);

    std::cout << std::endl << std::endl << "Resultat: " << s << "t -> " << res << std::endl << std::endl;
  }

  if(choice == 2) {
    string hour;
    string min;
    string sec;

    std::cout << "Heure > ";
    std::cin >> hour;
    std::cout << "Minutes > ";
    std::cin >> min;
    std::cout << "Secondes > ";
    std::cin >> sec;

    string res = convertRTToTicks(hour, min, sec);
    std::cout << std::endl << "Resultat: " << hour << ":" << min << ":" << sec << " -> " << res << "t" << std::endl << std::endl;
  }

  char exit;
  std::cout << "Appuyez sur n'importe quelle touche puis [ENTREE] pour quitter... ";
  std::cin >> exit;

  return 0;
}

int main(int argc, char** argv) {
  auto start = std::chrono::high_resolution_clock::now();
  // Throw every argument from index 1 into a vector
  std::vector<string> args;
  for(int i = 1; i < argc; i++) {
    args.push_back(argv[i]);
  }
  int argslength = static_cast<int>(args.size());

  // If no CLI argument, use IM
  if(argslength == 0) {
    std::cout << std::endl;
    return interactiveMode();
  }

  // If CLI arguments, use CLIM
  bool help = false;
  bool ticks = false;
  string tickArg;
  bool rt = false;
  string hour;
  string min;
  string sec;
  for(int i = 0; i < argslength; i++) {
    // Check for the arguments
    string* argument = &args.at(i);

    if(argument->at(0) == '-' && argument->at(1) == 'h') {
      help = true;
      break;
    }
    if(argument->at(0) == '-' && argument->at(1) == 't') {
      ticks = true;
      if(i + 1 < argslength) {
        tickArg = args.at(i + 1);
      } else {
        return 1;
      }
      break;
    }
    if((*argument).at(0) == '-' && (*argument).at(1) == 'T') {
      rt = true;
      if(i + 1 < argslength) {
        hour = args.at(i + 1);
      } else {
        return 1;
      }

      if(i + 2 < argslength) {
        min = args.at(i + 2);
      }

      if(i + 3 < argslength) {
        sec = args.at(i + 3);
      }
      break;
    }
  }

  // Prioritise -h
  if(help) {
    displayHelp();
    return 0;
  }

  // If no -h, then prioritise -t
  if(ticks) {
    string s = convertTicksToRT(tickArg);
    std::cout << std::endl << "Resultat: " << tickArg << "t -> " << s << std::endl << std::endl;
  }

  // If no -t, use -T
  if(rt) {
    string s = convertRTToTicks(hour, min, sec);
    std::cout << std::endl << "Resultat: " << hour << ":" << min << ":" << sec << " -> " << s << "t" << std::endl << std::endl;
  }

  auto end = std::chrono::high_resolution_clock::now();
  auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
  std::cout << "Temps de calcul: " << duration.count() << " microsecondes" << std::endl << std::endl;

  return 0;
}