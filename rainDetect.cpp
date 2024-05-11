#include <iostream>
#include <fstream>
#include <string>
#include <map>

using namespace std;

string test = "dkdk";

map<string,string> loadConfig(){
  ifstream configFile("./.config");
  map<string, string> config;

  if (!configFile.is_open()) {
      std::cerr << "Error: Unable to open config file" << std::endl;
      return config;
  }

  string line;
  while(getline(configFile, line)){

    if(line.empty() || line[0] == '#') continue;

    for(int i = 0; i < line.length(); i++){
      if(line[i] == '='){
        string key = line.substr(0,i);
        string value = line.substr(++i);
        config[key] = value;
        break;
      }
    }
  }

  return config;
}

int main() {
  loadConfig();
  return 0;
}