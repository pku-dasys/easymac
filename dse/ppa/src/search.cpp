#include <bits/stdc++.h>
using namespace std;

const int MAX_BIT=80;
const int MAX_DEP=50;
const int MAX_SEQ_LEN=500;
struct SEQ
{
  int n,level;
  int s[MAX_SEQ_LEN];
  void check_level(int bits)
  {
    int tmp_level[MAX_BIT],tmp_lowbit[MAX_BIT];
    memset(tmp_level,0,sizeof(tmp_level));
    for(int _=0;_<bits;_++)tmp_lowbit[_]=_;
    for(int i=1;i<=n;i++)
    {
      int x=s[i],y=tmp_lowbit[s[i]]-1;
      tmp_level[s[i]]=max(tmp_level[x],tmp_level[y])+1;
      level=max(tmp_level[s[i]],level);
      tmp_lowbit[s[i]]=tmp_lowbit[y];
    }
  }
};
SEQ nxt;
vector<SEQ> G[MAX_BIT],G2;
int low_bit[MAX_BIT];/*第i位区间为[low_bit[i],i]*/
int can_use[MAX_BIT];/*根据拓扑序规则，是否可以使用当前节点*/

int min_area[MAX_DEP];/*dep=i时，min_area*/

void init_dfs(int bits)
{
  for(int _=0;_<bits;_++)
  {
    low_bit[_]=_;
    can_use[_]=1;
  }
}

void print_now()
{
	int i;
	for(i=1;i<=nxt.n;i++)cout<<nxt.s[i]<<" ";cout<<endl;
}

void dfs(SEQ now,int pos,int bits,int now_level)/**/
{
  if(low_bit[bits-1]==0&&pos==now.n+1)
  {
    G[bits].push_back(nxt);
    //print_now();cout<<nxt.level<<endl;
    return ;
  }

  if(low_bit[bits-1]>0/*&&can_use[low_bit[bits-1]-1]>=now_level*/&&can_use[low_bit[bits-1]-1]==pos)
  {
    int u,v;
    u=low_bit[bits-1];
    low_bit[bits-1]=low_bit[low_bit[bits-1]-1];

    nxt.s[++nxt.n]=bits-1;
    dfs(now,pos,bits,now_level);
    low_bit[bits-1]=u;
    nxt.s[nxt.n--]=0;
  }

  if(pos<=now.n)
  {
    int now_bit=now.s[pos];
    nxt.s[++nxt.n]=now_bit;
    int now_low_bit=low_bit[now_bit]-1;
    int u,v,x,y;

    u=low_bit[now_bit];
    low_bit[now_bit]=low_bit[low_bit[now_bit]-1];

    if(now_low_bit<0){cout<<"GG";while(1);}
    
    y=can_use[now_bit];
    can_use[now_bit]=pos+1;
    //can_use[now_bit]=can_use[now_low_bit]+1;
    dfs(now,pos+1,bits,max(can_use[now_bit],now_level));

    low_bit[now_bit]=u;

    nxt.s[nxt.n--]=0;
    can_use[now_bit]=y;
    
  }
}

int get_num(char* s)
{
	int l=strlen(s),i,x=0;
	for(i=0;i<l;i++)x=x*10+s[i]-'0';
	//cout<<x<<endl;
	return x;
}

string get_str(int x)
{
	if(x==0)return "0";
	else
	{
		string ans="";
		int i=1;
		while(i*10<=x)i*=10;
		while(i)
		{
			ans+='0'+(x/i)%10;
			i/=10;
		}
		return ans;
	}
}

int main(int argc,char* argv[])
{
  string dir = "../../benchmarks/ppa/";
  int now_bit = get_num(argv[1]);
  int file_num = get_num(argv[2]);
  
  if(now_bit < 0){return -1;}
  if(file_num < 0){return -1;}
  if(now_bit > 80){return -1;}
  if(file_num > 500){return -1;}
  
  int delta;
  if(now_bit<16)delta=5;
  else if(now_bit<20)delta=3;
  else if(now_bit<32)delta=2;
  else delta=1;
  
  SEQ init_seq;
  init_seq.n=1;
  init_seq.s[1]=1;
  init_seq.level=1;
  G[2].push_back(init_seq);

  nxt.n=0;

  for(int b=3;b<=now_bit;b++)
  {
    for(int _=0;_<G[b-1].size();_++)
    {
      init_dfs(b);
      dfs(G[b-1][_],1,b,1);
    }
    int now_max_dp=0,now_min_dp=MAX_DEP;
    memset(min_area,0,sizeof(min_area));
    for(int _=0;_<G[b].size();_++)
    {
    	G[b][_].check_level(b);
      int a=G[b][_].n;
      int d=G[b][_].level;
      now_max_dp=max(now_max_dp,d);
      now_min_dp=min(now_min_dp,d);
      if(min_area[d]==0||a<min_area[d])
        min_area[d]=a;
    }
    cout<<b<<" "<<G[b].size()<<" ";
    //for(int d=0;d<=now_max_dp;d++)cout<<min_area[d]<<" ";cout<<endl;
    
    G2.clear();
    for(int _=0;_<G[b].size();_++)
    {
      int a=G[b][_].n;
      int d=G[b][_].level;
      if(a==min_area[d]&&d<=now_min_dp+2)G2.push_back(G[b][_]);
    }
    G[b].clear();
    for(int _=0;_<G2.size();_++)
      G[b].push_back(G2[_]);
    
    cout<<G[b].size()<<endl;
      
    /*for(int _=0;_<G[b].size();_++)
      for(int d=G[b][_].level;d<=now_max_dp;d++)
        min_area=min(min_area,G[b][_].n);*/
  }
  int b=now_bit;
  for(int _=0;_<G[b].size() && _<file_num;_++)
  {
    FILE *f;
    f=fopen((dir + get_str(now_bit) + "output"+get_str(_)).c_str(),"w");
    fprintf(f,"%s\n", argv[1]);
  	fprintf(f,"%d\n",G[b][_].n);
  	for(int i=1;i<=G[b][_].n;i++)
  	  fprintf(f,"%d ",G[b][_].s[i]);
  	fclose(f);
	  //cout<<G[b][_].s[i]<<" ";cout<<":";
    //cout<<G[b][_].n<<" "<<G[b][_].level<<endl;
  }
  cout<<"Total number: " << G[b].size() << endl;
  return 0;
}
