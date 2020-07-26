package eggplant4;

public class Macro {

    public String state;

    public Macro() {
        this.state = "DEFAULT";
    }

    String getMacroState (int _macroID) {
        if (_macroID == 1) return "DEFENSE";
        if (_macroID == 2) return "EXPLORATION";
        if (_macroID == 3) return "CONQUEST";
        if (_macroID == 4) return "DESTRUCTION";
        return "DEFAULT";
    }

    int getMacroID (String _state) {
        if (_state.equals("DEFENSE"))     return 1;
        if (_state.equals("EXPLORATION")) return 2;
        if (_state.equals("CONQUEST"))    return 3;
        if (_state.equals("DESTRUCTION")) return 4;
        return 0; // DEFAULT
    }

}
