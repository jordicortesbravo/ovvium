import { Platform } from "react-native";

// Use this for E2E tests using tools like Firebase Testlab, Detox...
// Test id is used by iOS, on Android we use accessibilityLabel as no resource-id is available yet on RN
export default function testID(id: string) {
    return Platform.OS === 'android' ?
      { accessible: true, accessibilityLabel: id } :
      { testID: id }
  }