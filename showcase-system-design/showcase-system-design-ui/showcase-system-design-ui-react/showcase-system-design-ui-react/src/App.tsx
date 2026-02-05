import {Admin, ListGuesser, Resource} from "react-admin";
import { Layout } from "./Layout";
import { dataProviderJson } from "./dataProvider";

export const App = () => (
  <Admin layout={Layout} dataProvider={dataProviderJson}>
    <Resource name="users" list={ListGuesser}/>
    <Resource name="posts" list={ListGuesser}/>
  </Admin>
);
