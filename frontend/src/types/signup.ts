export interface OAuth2SignUp {
  nickName: string;
  phoneNumber?: string;
  gender: "MALE" | "FEMALE";
  birthDate: string; // ISO string (YYYY-MM-DD)
  zipCode: string;
  address: string;
  detailAddress: string;
  uuid: string;
}
