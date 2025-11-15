import { createMuiTheme } from "@material-ui/core";
import { PaletteOptions } from "@material-ui/core/styles/createPalette";

export const ovviumTheme = createMuiTheme({
    palette: {
      primary: {
          main: '#F5B800'
      },
      secondary: {
          main: '#F5B800'
      },
      text: {
          primary: '#e8e8e8',
          secondary: '#F5B800'
      },
      background: {
          default:'#3c3c3c',
          paper:'#3c3c3c'
      }
    } as PaletteOptions
  });