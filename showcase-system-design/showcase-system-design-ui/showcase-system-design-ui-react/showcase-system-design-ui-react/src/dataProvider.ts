import simpleRestProvider from "ra-data-simple-rest";
import jsonRequestProvider from "ra-data-json-server"

export const dataProviderRest = simpleRestProvider(
  import.meta.env.VITE_SIMPLE_REST_URL,
);

export const dataProviderJson = jsonRequestProvider(
  import.meta.env.VITE_SIMPLE_REST_URL,
);
