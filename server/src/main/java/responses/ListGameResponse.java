package responses;

import model.*;
import java.util.ArrayList;

public record ListGameResponse(ArrayList<GameData> games) {
}
